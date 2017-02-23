package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

public class NullMessage extends Message {

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NullMessage;
    }
}
