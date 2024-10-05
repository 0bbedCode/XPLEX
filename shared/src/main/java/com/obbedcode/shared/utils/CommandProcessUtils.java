package com.obbedcode.shared.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class CommandProcessUtils {
    public static String readProcessOutput(Process process) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }catch (Exception ignored) { }
        finally { StreamUtils.close(reader); }
        return sb.toString();
    }


    public static boolean isDigit(char c) { return Character.isDigit(c); }
    public static boolean isAlpha(char c) { return Character.isAlphabetic(c); }
    public static boolean isSpace(char c) { return c == ' ' || c == '\t' || c == '\b'; }
    public static boolean isValidTimeValue(boolean lastWasTime, char c) { return lastWasTime && (c == ' ' || c == '-' || c == ':' || c == '+' || c == '.' || Character.isDigit(c)); }
    public static boolean isValidDeviceValue(boolean lastWasDevice, char c) { return lastWasDevice && (c == '/' || Character.isDigit(c) || Character.isAlphabetic(c)); }

    public static class FieldPointer {
        public boolean lastWasTime = false;
        public boolean lastWasDevice = false;
        public boolean lastWasInode = false;
        public boolean expectingValue = false;
        public String field = null;

        public void ensureField(String fieldName) {
            if("inode:".equalsIgnoreCase(fieldName)) {
                expectingValue = true;
                lastWasInode = true;
                field = "Inode";
            }
            else if("device:".equalsIgnoreCase(fieldName)) {
                lastWasDevice = true;
                expectingValue = true;
                field = "Device";
            }
            else if("access:".equalsIgnoreCase(fieldName) || "modify:".equalsIgnoreCase(fieldName) || "change:".equalsIgnoreCase(fieldName) || "birth:".equalsIgnoreCase(fieldName)) {
                lastWasTime = true;
                expectingValue = true;
                field = fieldName.substring(0, fieldName.length() - 1);
            } else {
                reset();
            }
        }

        public void reset() {
            lastWasTime = false;
            lastWasDevice = false;
            expectingValue = false;
            lastWasInode = false;
            field = null;
        }
    }


    public static String generateNumber(int low, int high) {
        int num = ThreadLocalRandom.current().nextInt(low, high);
        if(num <= 9) {
            return "0" + String.valueOf(num);
        } else {
            return String.valueOf(num);
        }
    }

    public static String randomizeStat(String input) {
        StringBuilder currentChunk = new StringBuilder();
        StringBuilder full = new StringBuilder();
        StringBuilder value = new StringBuilder();

        int lastIndex = input.length() - 1;

        FieldPointer ptr = new FieldPointer();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if(ptr.expectingValue) {
                boolean added = false;
                if(isValidTimeValue(ptr.lastWasTime, c) ||
                        isValidDeviceValue(ptr.lastWasDevice, c) ||
                        (ptr.expectingValue && Character.isDigit(c))) {

                    if(ptr.lastWasTime && c == '-' && value.length() == 0) {
                        //Skip
                    } else {
                        value.append(c);
                        added = true;
                    }
                }

                if(!added || i == lastIndex) {
                    if(value.length() > 0) {
                        //System.out.println("[1] Field: [" + ptr.field + "] Value: [" + value + "]");
                        full.append(cleanValue(ptr, value.toString()));
                        value = new StringBuilder();
                    }

                    if(!added) {
                        full.append(c);
                    }

                    ptr.reset();
                }

                continue;
            }

            if(c == '\n') {
                if(ptr.expectingValue || value.length() > 0) {
                    //System.out.println("[2] Field: [" + ptr.field + "] Value: [" + value + "]");
                    //full.append(value);
                    full.append(cleanValue(ptr, value.toString()));
                    value = new StringBuilder();
                    ptr.reset();
                }

                if(currentChunk.length() > 0) {
                    full.append(currentChunk);
                    currentChunk = new StringBuilder();
                }
            }

            if(c == ' ') {
                if(currentChunk.length() > 0) {
                    String cChunk = currentChunk.toString();
                    ptr.ensureField(cChunk);
                    currentChunk = new StringBuilder();
                    full.append(cChunk);
                } else if(value.length() > 0) {
                    //End of Value like Inode
                    //full.append(value);
                    //System.out.println("[3] Field: [" + ptr.field + "] Value: [" + value + "]");
                    full.append(cleanValue(ptr, value.toString()));
                    value = new StringBuilder();
                    ptr.reset();
                }
            }

            if(Character.isAlphabetic(c) || currentChunk.length() > 0) {
                currentChunk.append(c);
                continue;
            }

            full.append(c);
        }

        //System.out.println("\nOld:\n" + input);
        //System.out.println("\n\nNew:\n" + full);
        return full.toString();
    }

    private static String cleanValue(FieldPointer ptr, String value) {
        if(ptr.lastWasTime) {
            String val = value.toString();
            if(val.length() > 8) {
                String[] halfs = val.split(" ");
                //String[] tops = halfs[0].split("-");

                StringBuilder sb = new StringBuilder();
                sb.append(generateNumber(1969, 2024));
                sb.append("-");
                sb.append(generateNumber(1, 12));
                sb.append("-");
                sb.append(generateNumber(1, 29));
                sb.append(" ");

                String lowHalf = halfs[1];
                if(lowHalf.contains(":")) {
                    String[] lowParts = lowHalf.split(":");
                    StringBuilder lowBuild = new StringBuilder();
                    int sz = lowParts.length - 1;
                    for(int j = 0; j < lowParts.length; j++) {
                        String el = lowParts[j];
                        if(el.contains(".")) {
                            //Decimal big value
                            String[] decParts = el.split("\\.");
                            if(decParts.length > 1) {
                                String decimalEnd = decParts[1];
                                lowBuild.append(generateNumber(0, 56));
                                lowBuild.append(".");
                                boolean allZeros = ThreadLocalRandom.current().nextBoolean();

                                if(allZeros) {
                                    lowBuild.append("000000000");
                                } else {
                                    lowBuild.append(generateNumber(100000000, 999999999));
                                }
                            } else {
                                lowBuild.append(generateNumber(0, 56));
                            }
                        } else {
                            lowBuild.append(generateNumber(10, 18));
                        }

                        if(j != sz) {
                            lowBuild.append(":");
                        }
                    }

                    sb.append(lowBuild);
                } else {
                    sb.append(halfs[1]);
                }

                return sb.toString();
                //full.append(sb);
                //value = new StringBuilder();
            }
        } else if(ptr.lastWasDevice) {
            String low = generateNumber(100, 1000);
            String hig = generateNumber(1000, 9999);
            return low + "h/" + hig + "d";
            //full.append(low + "h/" + hig + "d");
            //value = new StringBuilder();
        } else if(ptr.lastWasInode) {
            //full.append(generateNumber(58, 9999999));
            //value = new StringBuilder();
            return generateNumber(58, 9999999);
        } else {
            //full.append(value);
            //value = new StringBuilder();
            return value;
        }

        return value;
    }
}
