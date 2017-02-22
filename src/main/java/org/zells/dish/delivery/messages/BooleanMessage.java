package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

public class BooleanMessage extends Message {

    private boolean value;

    public BooleanMessage(boolean value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value ? "true" : "";
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + (value ? 1 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BooleanMessage
                && value == ((BooleanMessage) obj).value;
    }
}
