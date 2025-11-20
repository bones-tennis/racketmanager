package com.example.racketmanager.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String SECRET_KEY = "fT7$Pz1Qw9@bL2x!";
    private static final String SALT = "deadbeefcafebabe";

    private static SecretKeySpec getKey() throws Exception {
        byte[] key = (SECRET_KEY + SALT).getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(Arrays.copyOf(key, 16), "AES");
    }

    public static String encrypt(String input) {
        if (input == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("暗号化エラー", e);
        }
    }

    public static String decrypt(String input) {
        if (input == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(Base64.getDecoder().decode(input)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("復号化エラー", e);
        }
    }
}
