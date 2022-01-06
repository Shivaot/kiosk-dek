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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
        File file = res.getFile();
        PdfReader reader = new PdfReader(Files.readAllBytes(Paths.get(file.getPath())));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, output);
        AcroFields form = stamper.getAcroFields();

        form.setField("product_date", product.getDate().toString());
        form.setField("product_part_no", product.getPartNumber());
        form.setField("paroduct_modal", product.getModel().getDisplayValue());
        form.setField("product_qty", product.getQuantity().toString());
        form.setField("product_problem_desc", product.getDefectDescription());
        form.setField("product_creator_name", product.getCreatorName());
        PdfContentByte content = stamper.getOverContent(reader.getNumberOfPages());

        com.itextpdf.text.Image image = Image.getInstance(barcodeBytes);

        image.setAbsolutePosition(142, 570);
        image.scaleAbsolute(140, 20);
        content.addImage(image);
        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();
        return output.toByteArray();


    }


}
