package com.obbedcode.shared.random;

import android.util.Log;

import java.util.Locale;
import java.util.Random;


public class RandomNetworkData {
    private static final String TAG = "ObbedCode.XP.RandomNetworkData";

    private static final String DEFAULT_MAC = "00:1A:2B:3C:4D:5E";
    private static final int DEFAULT_IP_INT = 0x7F000001;
    private static final String DEFAULT_IP = "127.0.0.1";

    private static final Random random = new Random();

    public static String macBytesToString(byte[] bys) {
        StringBuilder sb = new StringBuilder(18);
        for (byte b : bys) {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     *
     * MAC Format Can Include '-' or ':' like 00:00:00:00:00:00 or 00-00-00-00-00-00
     * If it has anything more , and or uses Invalid Format Function will default to use 00:1A:2B:3C:4D:5E
     * If 'random' is Passed through instead of a MAC Address then a Random MAC Address Will be used/Generated
     *
     * @param mac The String MAC address.
     * @return The byte array representation of the MAC address.
     */
    public static byte[] macStringToBytes(String mac) {
        byte[] macBytes = new byte[6];
        try {
            if(mac == null || mac.equals(" "))
                mac = DEFAULT_MAC;
            else if(mac.equals("random"))
                mac = generateRandomMacString();
            else if(!mac.equals(DEFAULT_MAC) && (mac.length() > 17 || mac.length() < 12))
                mac = DEFAULT_MAC;

            String normalizedMac = removeAllNonHexadecimalChars(mac);
            for (int i = 0; i < 6; i++) {
                // Parse each pair of hexadecimal characters
                int index = i * 2;
                int value = Integer.parseInt(normalizedMac.substring(index, index + 2), 16);
                macBytes[i] = (byte) value;
            }

            return macBytes;
        }catch (Exception e) {
            Log.e(TAG, "Failed to convert string mac to mac bytes! \n" + e + "\n" + Log.getStackTraceString(e));
            return macBytes;
        }
    }

    /**
     * Converts an String IP address to a int representation.
     * If Format of the Ip Address is Invalid then it will Default to '127.0.0.1'
     * If 'random' is Passed through instead of a IpAddress then a Random IpAddress Will be used/Generated
     *
     * @param ip The String IP address. (127.0.0.1)
     * @return The int representation of the IP address.
     */
    public static int stringIpAddressToInt(String ip) {
        try {
            if(ip == null || ip.equals(" "))
                return DEFAULT_IP_INT;

            String[] octets = ip.split("\\.");
            int result = 0;
            for (int i = 0; i < octets.length; i++) {
                int octet = Integer.parseInt(octets[i]);
                result |= octet << (24 - 8 * i);
            }
            return result;
        }catch (Exception e) {
            Log.e(TAG, "Failed to convert string IpAddress to IpAddress int! \n" + e + "\n" + Log.getStackTraceString(e));
            return DEFAULT_IP_INT;
        }
    }

    /**
     * Converts an String IP address to a byte array representation.
     * If Format of the Ip Address is Invalid then it will Default to '127.0.0.1'
     * If 'random' is Passed through instead of a IpAddress then a Random IpAddress Will be used/Generated
     *
     * @param ip The String IP address. (127.0.0.1)
     * @return The byte array representation of the IP address.
     */
    public static byte[] stringIpAddressToBytes(String ip) {
        byte[] bytes = new byte[4];
        try {
            if(ip == null || ip.equals(" ")) ip = DEFAULT_IP;
            else if(ip.equals("random"))  ip = generateRandomIpAddress();

            String[] parts = ip.split("\\.");
            if(parts.length != 4) parts = DEFAULT_IP.split("\\.");

            for (int i = 0; i < 4; i++) {
                int part = Integer.parseInt(parts[i]);
                if (part < 0 || part > 255) {
                    throw new IllegalArgumentException("Invalid IP address part: " + parts[i]);
                }
                bytes[i] = (byte) part;
            }

            return bytes;
        }catch (Exception e) {
            Log.e(TAG, "Failed to convert string IpAddress to IpAddress int! \n" + e + "\n" + Log.getStackTraceString(e));
            if(!ip.equals("127.0.0.1"))
                return stringIpAddressToBytes("127.0.0.1");

            return bytes;
        }
    }


    /**
     * Converts an integer IP address to a string representation.
     * Assumes the integer is in big-endian format (network byte order).
     *
     * @param ip The integer IP address.
     * @return The string representation of the IP address.
     */
    public static String intIpAddressToString(int ip) {
        return String.format(Locale.ROOT, "%d.%d.%d.%d",
                (ip >> 24) & 0xFF, // Extracts the first byte
                (ip >> 16) & 0xFF, // Extracts the second byte
                (ip >> 8) & 0xFF,  // Extracts the third byte
                ip & 0xFF);
    }

    /**
     * Generates a random IP address.
     *
     * @return A string representing a random IP address.
     */
    public static String generateRandomIpAddress() {
        return String.format(Locale.ROOT, "%d.%d.%d.%d",
                random.nextInt(256), // Random number for the first octet (0-255)
                random.nextInt(256), // Random number for the second octet
                random.nextInt(256), // Random number for the third octet
                random.nextInt(256)); // Random number for the fourth octet
    }

    /**
     * Generates a random MAC address.
     *
     * @return A string representing a random MAC address.
     */
    public static String generateRandomMacString() {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }

    public static String removeAllNonHexadecimalChars(String str) {
        return str.replaceAll("[^0-9A-Fa-f]", "");
    }
}
