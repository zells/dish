package org.zells.dish.util;

public class ByteArray {
    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    public static byte[] fromHexString(String hexString) {
        hexString = hexString.replace("-", "");
        if (hexString.startsWith("0x")) {
            hexString = hexString.substring(2);
        }

        int len = hexString.length();

        if (len % 2 != 0) {
            throw new RuntimeException("Invalid hex string: " + hexString);
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return "0x" + new String(hexChars);
    }
}
