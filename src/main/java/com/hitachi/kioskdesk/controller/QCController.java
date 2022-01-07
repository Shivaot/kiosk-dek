package com.hitachi.kioskdesk.controller;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.helper.Message;
import com.hitachi.kioskdesk.repository.ProductRepository;
import com.hitachi.kioskdesk.service.StickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Shiva Created on 04/01/22
 */
@Slf4j
@Controller
@RequestMapping("/qc/")
public class QCController {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    StickerService stickerService;

    @GetMapping("product/{id}")
    public String viewProduct(Model model, @PathVariable(value = "id") Long productId, HttpSession session) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        try {
            Product product = optionalProduct.get();
            if (product.getStatus() != Status.NEW) {
                log.debug("Error while opening product on QC end {}", product.getStatus());
                session.setAttribute("message", new Message("Status Invalid for QC to view!! ", "alert-danger"));
                return "error/403";
            }
            model.addAttribute("title", "Kiosk - QC Product");
            model.addAttribute("product", product);
            return "qc";
        } catch (NoSuchElementException ex) {
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + productId, "alert-danger"));
            return "error/404";
        }
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                                HttpSession session, Model model) {

        try {
            if (result.hasErrors()) {
                log.error("Validation error while updating product {}", result);
                model.addAttribute("product", product);
                return "qc";
            }
            product.setStatus(Status.QC);
            product.setLastUpdated(new Date(System.currentTimeMillis()));
            Product savedProduct = productRepository.save(product);
            log.info("Product updated successfully with id {}", savedProduct.getId());
            model.addAttribute("product", product);
            model.addAttribute("isNewProduct", false);
            session.setAttribute("message", new Message("Successfully Updated Product", "alert-success"));
        } catch (Exception ex) {
            log.error("Error while saving product", ex);
            model.addAttribute("product", product);
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "new-item";
        }
        model.addAttribute("title", "Kiosk - Product Saved");
        return "success";
    }

    @RequestMapping(value = "/product/print", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getRedStickerFile(@Param(value = "id") Long id) throws Exception {
        Product product = productRepository.findById(id).get();
        byte[] stickerBytes = stickerService.getRedStickerBytes(product);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=red_sticker.pdf");
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stickerBytes));
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

}
