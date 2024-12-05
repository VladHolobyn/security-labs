package com.holobyn.security.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private static final String ISSUER = "Your Company Name";


    public String generateKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Validate the TOTP code
    public boolean isValid(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build()
        );
        return gAuth.authorize(secret, code);
    }

    // Generate a QR code URL for Google Authenticator
    public byte[] generateQRUrl(String secret, String username) {
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
            ISSUER,
            username,
            new GoogleAuthenticatorKey.Builder(secret).build()
        );
        try {
            return generateQRCodeImage(url);
        } catch (Exception e) {
            return null;
        }
    }

    // Generate a QR code image in Base64 format
    public static byte[] generateQRCodeImage(String qrCodeText) throws WriterException, IOException  {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200);

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }
}
