package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

public class BinaryMessage extends Message {

    private byte[] value;

    public BinaryMessage(byte[] value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value.length == 0 ? null : new String(value);
    }

    @Override
    public byte[] asBinary() {
        return value;
    }
}
