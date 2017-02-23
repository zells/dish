package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.util.ByteArray;

import java.util.Arrays;

public class BinaryMessage extends Message {

    private byte[] value;

    public BinaryMessage(byte[] value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return ByteArray.toHexString(value);
    }

    @Override
    public boolean isTrue() {
        return value.length != 0;
    }

    @Override
    public byte[] asBytes() {
        return value;
    }

    @Override
    public Address asAddress() {
        return Address.fromBytes(value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BinaryMessage
                && Arrays.equals(value, ((BinaryMessage) obj).value);
    }

    public static BinaryMessage fromString(String hexString) {
        return new BinaryMessage(ByteArray.fromHexString(hexString));
    }
}
