package com.obbedcode.shared.utils;

import java.io.BufferedReader;

public class StreamUtils {
    public static void dispose(BufferedReader bf) {
        try {
            bf.close();
        }catch (Exception ignored) { }
    }
}
