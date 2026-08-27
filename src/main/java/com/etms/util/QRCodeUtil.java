package com.etms.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class QRCodeUtil {

    public static BufferedImage generateQRCodeImage(String text, int width, int height, Path filePath) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            if (filePath != null) {
                File outputFile = filePath.toFile();
                ImageIO.write(image, "png", outputFile);
            }
            return image;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeQRCode(Path filePath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}