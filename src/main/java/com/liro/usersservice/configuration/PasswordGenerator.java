package com.liro.usersservice.configuration;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return password.toString();
    }

    public static String generatePassword() {
        return generatePassword(DEFAULT_LENGTH);
    }
}