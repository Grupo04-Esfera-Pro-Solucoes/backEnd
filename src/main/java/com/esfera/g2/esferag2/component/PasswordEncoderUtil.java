package com.esfera.g2.esferag2.component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderUtil {

    public String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        String hashedPassword = encodePassword(rawPassword);
        return hashedPassword.equals(encodedPassword);
    }
}

