package com.obbedcode.shared.random;

import com.obbedcode.shared.Str;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
    public static final String NUMBERS = "0123456789";
    public static final String UPPER_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String HEX_CHARS = "0123456789ABCDEF";

    public static int generateNumber(int bound) { return ThreadLocalRandom.current().nextInt(bound); }
    public static int generateNumber(int origin, int bound) { return ThreadLocalRandom.current().nextInt(origin, bound); }

    public static String UUID() { return UUID.randomUUID().toString(); }

    public static String generateAlphaNumeric(int length) { return generateString(LETTERS + NUMBERS, length); }

    public static String generateHexString(int length) { return generateString(HEX_CHARS, length); }
    public static String generateNumberString(int length) { return generateString(NUMBERS, length); }
    public static String generateAlphaLowerString(int length) { return generateString(LOWER_LETTERS, length); }
    public static String generateAlphaUpperString(int length) { return generateString(UPPER_LETTERS, length); }
    public static String generateAlphaString(int length) { return generateString(LETTERS, length); }

    public static String generateString(String characters, int length) {
        if (length <= 0) return Str.EMPTY;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
