package com.hitachi.kioskdesk.service;

import com.hitachi.kioskdesk.domain.Product;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Shiva Created on 06/01/22
 */
@Service
@Slf4j
public class StickerService {

    public static byte[] generateCode128BarcodeImage(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);
        barcode.setBarHeight(50);
        barcode.setFont(null);

        BufferedImage bufferedImage = BarcodeImageHandler.getImage(barcode);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);

        return outputStream.toByteArray();
    }

    public byte[] getWhiteStickerBytes(Product product, String baseUrl) throws Exception {
        String barcodeContent = baseUrl + "/qc/product/" + product.getId();
        byte[] barcodeBytes = generateCode128BarcodeImage(product.getId().toString());
        log.info("Creating barcode with content {}", barcodeContent);
        return generateWhiteSticker(barcodeBytes, product);
    }

    byte[] generateWhiteSticker(byte[] barcodeBytes, Product product) throws DocumentException, IOException {
        ClassPathResource res = new ClassPathResource("white_sticker.pdf");
        InputStream file = res.getInputStream();
        PdfReader reader = new PdfReader(IOUtils.toByteArray(file));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, output);
        AcroFields form = stamper.getAcroFields();

        form.setField("product_date", product.getDate().toString());
        form.setField("product_part_no", product.getPartNumber());
        form.setField("product_model", product.getPartDescription());
        form.setField("product_qty", product.getQuantity().toString());
        form.setField("product_problem_desc", product.getDefectDescription());
        form.setField("product_creator_name", product.getCreatorName());
        form.setField("product_id", product.getId().toString());
        PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages());

        com.itextpdf.text.Image image = Image.getInstance(barcodeBytes);

        image.setAbsolutePosition(5, 647);
        image.scaleAbsolute(130, 26);
        content.addImage(image);
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        return output.toByteArray();
    }


    public byte[] getRedStickerBytes(Product product) throws Exception {
        byte[] barcodeBytes = generateCode128BarcodeImage(product.getId().toString());
        log.info("Creating barcode with for QC with id {}", product.getId());
        return generateRedSticker(barcodeBytes, product);
    }

    byte[] generateRedSticker(byte[] barcodeBytes, Product product) throws DocumentException, IOException {
        ClassPathResource res = new ClassPathResource("red_sticker.pdf");
        InputStream file = res.getInputStream();
        PdfReader reader = new PdfReader(IOUtils.toByteArray(file));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, output);
        AcroFields form = stamper.getAcroFields();

        form.setField("product_date", product.getDate().toString());
        form.setField("product_part_no", product.getPartNumber());
        form.setField("product_model", product.getPartDescription());
        form.setField("product_qty", product.getQuantity().toString());
        form.setField("product_reason", product.getQc1Comments());
        form.setField("product_notification_no", product.getNotificationNumber());
        form.setField("product_vendor", product.getVendor());
        form.setField("product_authorized_by", product.getAuthorizedBy());
        form.setField("product_id", product.getId().toString());
        form.setField("rejectionOrScrap", product.getRejectionOrScrap().getDisplayValue().toUpperCase());

        PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages());

        com.itextpdf.text.Image image = Image.getInstance(barcodeBytes);

        image.setAbsolutePosition(5, 647);
        image.scaleAbsolute(130, 27);
        content.addImage(image);
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        return output.toByteArray();
    }
}
