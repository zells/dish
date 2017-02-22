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
    public byte[] asBytes() {
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

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompositeMessage
                && map.equals(((CompositeMessage) obj).map);
    }
}
