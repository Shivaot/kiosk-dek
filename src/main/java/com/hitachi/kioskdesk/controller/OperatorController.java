package com.hitachi.kioskdesk.controller;

import com.hitachi.kioskdesk.domain.Product;
import com.hitachi.kioskdesk.enums.Status;
import com.hitachi.kioskdesk.helper.Message;
import com.hitachi.kioskdesk.repository.ProductModelRepository;
import com.hitachi.kioskdesk.repository.ProductRepository;
import com.hitachi.kioskdesk.service.StickerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Date;

/**
 * Shiva Created on 04/01/22
 */
@Slf4j
@Controller
@RequestMapping("/operator/")
public class OperatorController {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductModelRepository productModelRepository;
    @Autowired
    StickerService stickerService;

    @RequestMapping({"/new-item", "/"})
    public String newItem(Model model) {
        model.addAttribute("title", "Kiosk - New Product");
        Product product = new Product();
        product.setDate(new Date(System.currentTimeMillis()));
        model.addAttribute("product", product);
        model.addAttribute("productModels", productModelRepository.findAll());
        return "new-item";
    }


    @PostMapping("/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                             HttpSession session, Model model, Principal principal) {

        try {
            if (result.hasErrors()) {
                log.info("Operator with ID {}", principal.getName());
                log.error("Validation error while saving product {}", result);
                model.addAttribute("product", product);
                model.addAttribute("productModels", productModelRepository.findAll());
                return "new-item";
            }
            product.setStatus(Status.NEW);
            Product savedProduct = productRepository.save(product);
            log.info("Operator with ID {}", principal.getName());
            log.info("Product saved successfully with id {}", savedProduct.getId());
            model.addAttribute("product", product);
            model.addAttribute("isNewProduct", true);
            session.setAttribute("message", new Message("Successfully Added Product", "alert-success"));
        } catch (Exception ex) {
            log.info("Operator with ID {}", principal.getName());
            log.error("Error while saving product", ex);
            model.addAttribute("product", product);
            model.addAttribute("productModels", productModelRepository.findAll());
            session.setAttribute("message", new Message("Something went wrong!! " + ex.getMessage(), "alert-danger"));
            return "new-item";
        }
        model.addAttribute("title", "Kiosk - Product Saved");
        return "success";
    }

    @RequestMapping(value = "/product/print", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getWhiteStickerFile(@Param(value = "id") Long id, Principal principal) throws Exception {
        Product product = productRepository.findById(id).get();
        byte[] stickerBytes = stickerService.getWhiteStickerBytes(product, getBaseUrl());
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=white_sticker_" + id + ".pdf");
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stickerBytes));
        log.info("Operator Sticker with ID {}", principal.getName());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    private void cropStickerFromPdfAndWrite(byte[] stickerBytes) throws IOException {
        PDDocument document = PDDocument.load(stickerBytes);
        PDFRenderer pr = new PDFRenderer(document);
        BufferedImage bufferedImage = pr.renderImageWithDPI(0, 300, ImageType.RGB);
        File croppedImageFile = new File("/home/shiva/Music/cropped.jpg");

        ImageIO.write(bufferedImage.getSubimage(400, 470, 800, 850), "jpg", croppedImageFile);
    }

    private String getBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
