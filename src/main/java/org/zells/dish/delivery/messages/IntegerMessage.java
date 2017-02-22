package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

import java.nio.ByteBuffer;

public class IntegerMessage extends Message {

    private int value;

    public IntegerMessage(int value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return Integer.toString(value);
    }

    @Override
    public boolean isTrue() {
        return value != 0;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public byte[] asBytes() {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntegerMessage
                && value == ((IntegerMessage) obj).value;
    }
}
