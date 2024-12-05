package com.holobyn.security.service;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "my-very-secure-custom-key123";

    // Generate a random AES key

    // Encrypt the data
//    public static String encrypt(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
//        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }

    // Decrypt the data
//    public static String decrypt(String encryptedData, String key) throws Exception {
//        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] originalData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
//        return new String(originalData);
//    }
}
