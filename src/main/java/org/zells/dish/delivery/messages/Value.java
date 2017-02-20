package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

import java.util.Set;

public class Value implements Message {

    private String value;

    public Value(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public Set<String> keys() {
        throw new RuntimeException("Is a value");
    }

    public Message read(String key) {
        throw new RuntimeException("Is a value");
    }
}
