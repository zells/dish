package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

public class StringMessage extends Message {

    private String value;

    public StringMessage(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    @Override
    public byte[] asBinary() {
        return isNull() ? super.asBinary() : value.getBytes();
    }
}
