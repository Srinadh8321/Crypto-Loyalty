package com.example.cryptoloyalty.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class KeyEncryptionService {

    private static final String SECRET = "CHANGE_ME_32_BYTE_SECRET";

    public String encrypt(String data) {
        return Base64.getEncoder()
                .encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String encrypted) {
        return new String(
                Base64.getDecoder().decode(encrypted),
                StandardCharsets.UTF_8
        );
    }
}
