package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeMessage extends Message {

    private Map<String, Message> map = new HashMap<String, Message>();

    public CompositeMessage put(String key, Message value) {
        map.put(key, value);
        return this;
    }

    @Override
    public String asString() {
        return map.toString();
    }

    @Override
    public byte[] asBinary() {
        return asString().getBytes();
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Message read(String key) {
        return map.get(key);
    }
}
