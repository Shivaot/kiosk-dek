package com.hitachi.kioskdesk.controller;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.helper.Message;
import com.hitachi.kioskdesk.helper.Utils;
import com.hitachi.kioskdesk.repository.ProductRepository;
import com.hitachi.kioskdesk.service.ProductService;
import com.hitachi.kioskdesk.service.StickerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @Autowired
    ProductService productService;


    @GetMapping("product/{id}")
    public String viewProduct(Model model, @PathVariable(value = "id") Long productId, HttpSession session, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        String name = principal.getName();
        try {
            Product product = optionalProduct.get();
            if (product.getStatus() == Status.NEW) {
                log.info("QC with ID {}", name);
                log.info("QC viewing NEW product with id {}", productId);
                model.addAttribute("title", "Kiosk - QC Product");
                product.setQcInspectionDate(new Date(System.currentTimeMillis()));
                product.setAuthorizedBy(Utils.parseUsername(principal));
                model.addAttribute("product", product);
                return "qc";
            } else if (product.getStatus() == Status.QC || product.getStatus() == Status.CANCELLED || product.getStatus() == Status.COMPLETED) {
                log.info("QC with ID {}", name);
                log.info("QC viewing QC/CANCELLED/COMPLETED product with id {}", productId);
                model.addAttribute("title", "Kiosk - QC Product");
                model.addAttribute("product", product);
                return "qc-qcComplete";
            } else {
                log.info("QC with ID {}", name);
                log.debug("Error while opening product on QC end {}", product.getStatus());
                session.setAttribute("message", new Message("Status Invalid for QC to view!! ", "alert-danger"));
                return "error/403";
            }
        } catch (NoSuchElementException ex) {
            log.info("QC with ID {}", name);
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + productId, "alert-danger"));
            return "error/404";
        }
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                                HttpSession session, Model model, Principal principal) {
        String name = principal.getName();

        try {
            if (result.hasErrors()) {
                log.info("QC with ID {}", name);
                log.error("Validation error while updating product {}", result);
                model.addAttribute("product", product);
                return "qc";
            }
            product.setStatus(Status.QC);
            product.setLastUpdated(new Date(System.currentTimeMillis()));
            Product savedProduct = productRepository.save(product);
            log.info("QC with ID {}", name);
            log.info("Product updated successfully with id {}", savedProduct.getId());
            model.addAttribute("product", product);
            model.addAttribute("isNewProduct", false);
            session.setAttribute("message", new Message("Successfully Updated Product", "alert-success"));
        } catch (Exception ex) {
            log.info("QC with ID {}", name);
            log.error("Error while saving product", ex);
            model.addAttribute("product", product);
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "new-item";
        }
        model.addAttribute("title", "Kiosk - Product Saved");
        return "success";
    }

    @RequestMapping(value = "/product/print", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getRedStickerFile(@Param(value = "id") Long id, Principal principal) throws Exception {
        Product product = productRepository.findById(id).get();
        byte[] stickerBytes = stickerService.getRedStickerBytes(product);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "attachment;filename=red_sticker_" + id + ".pdf");
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stickerBytes));
        log.info("QC with ID {}", principal.getName());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    @RequestMapping(value = "/productRedirect", method = RequestMethod.GET)
    public String productRedirect(@RequestParam String barcodeText, Principal principal) {
        log.info("QC with ID {}", principal.getName());
        log.info("Redirecting after scanning barcodeText {}", barcodeText);
        return "redirect:/qc/product/" + barcodeText;
    }


    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    public String qcHome(Model model) {
        model.addAttribute("title" ,"QC - Scan");
        return "qc_home";
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String listProducts(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Product> productPage = productService.findPaginated(PageRequest.of(currentPage - 1, pageSize), Status.QC);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("title", "QC - products");

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "qc-products";
    }


    @RequestMapping(value = "/product/cancel", method = RequestMethod.GET)
    public String cancelProduct(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        try {
            Product product = optionalProduct.get();
            product.setStatus(Status.CANCELLED);
            productRepository.save(product);
            log.info("QC with ID {}", principal.getName());
            log.info("QC cancelled product with id {}", id);
            session.setAttribute("message", new Message("Product Status Updated for " + id, "alert-success"));
            return "redirect:/qc/products";
        } catch (NoSuchElementException ex) {
            log.info("QC with ID {}", principal.getName());
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }

    @RequestMapping(value = "/products/cancelled", method = RequestMethod.GET)
    public String listCancelledProducts(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Product> productPage = productService.findPaginated(PageRequest.of(currentPage - 1, pageSize), Status.CANCELLED);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("title", "QC - products cancelled");

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "qc-products-cancelled";
    }

    @RequestMapping(value = "/product/delete", method = RequestMethod.GET)
    public String deleteProduct(@Param(value = "id") Long id, HttpSession session, Principal principal) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        try {
            Product product = optionalProduct.get();
            productRepository.delete(product);
            log.info("QC with ID {}", principal.getName());
            log.info("QC deleted product with ID {}", id);
            session.setAttribute("message", new Message("Product deleted successfully with ID" + id, "alert-success"));
            return "redirect:/qc/products/cancelled";
        } catch (NoSuchElementException ex) {
            log.error("Product not found ", ex);
            session.setAttribute("message", new Message("Product Not Found with ID " + id, "alert-danger"));
            return "error/404";
        }
    }
}
