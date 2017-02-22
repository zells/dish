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
    public byte[] asBytes() {
        return isNull() ? super.asBytes() : value.getBytes();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringMessage && value.equals(((StringMessage) obj).value);
    }
}
