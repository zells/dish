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
}
