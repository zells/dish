package org.zells.dish.util;

import java.util.Arrays;

public class Uuid {

    private byte[] bytes;

    public Uuid(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Uuid fromString(String hexString) {
        return new Uuid(ByteArray.fromHexString(hexString));
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return ByteArray.toHexString(bytes);
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
