package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeMessage extends Message {

    private Map<String, Message> map = new HashMap<String, Message>();

    public CompositeMessage(Message... messages) {
        int i = 0;
        for (Message message : messages) {
            put(i++, message);
        }
    }

    public CompositeMessage put(String key, Message value) {
        map.put(key, value);
        return this;
    }

    public CompositeMessage put(int key, Message value) {
        return put(Integer.toString(key), value);
    }

    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (String key : keys()) {
            builder.append(key).append(":").append(read(key).asString()).append(", ");
        }
        if (!keys().isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public byte[] asBytes() {
        return new byte[0];
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Message read(String key) {
        if (!map.containsKey(key)) {
            return new NullMessage();
        }
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
