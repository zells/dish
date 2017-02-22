package org.zells.dish.util;

import java.util.Arrays;

public class Uuid {

    final private static char[] hexArray = "0123456789abcdef".toCharArray();
    private byte[] bytes;

    public Uuid(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Uuid fromString(String hexString) {
        hexString = hexString.replace("-", "");

        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return new Uuid(data);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Uuid
                && Arrays.equals(bytes, ((Uuid) obj).bytes);
    }
}
