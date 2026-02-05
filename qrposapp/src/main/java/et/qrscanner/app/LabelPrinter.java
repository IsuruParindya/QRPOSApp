package et.qrscanner.app;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;

public class LabelPrinter {
    private static final int LABEL_WIDTH = 400;
    private static final int LABEL_HEIGHT = 200;

    public static BufferedImage generateQRCodeImage(String text) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 150, 150);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage generateLabel(String qrCode, String name, String price) throws WriterException {
        BufferedImage qrImg = generateQRCodeImage(qrCode);

        BufferedImage label = new BufferedImage(LABEL_WIDTH, LABEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = label.createGraphics();

        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, LABEL_WIDTH, LABEL_HEIGHT);

        g.drawImage(qrImg, 20, 20, null);

        g.setColor(java.awt.Color.BLACK);
        g.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        g.drawString(name, 180, 50);
        g.drawString(price, 180, 90);

        g.dispose();
        return label;
    }
}