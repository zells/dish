package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Structure implements Message {

    private Map<String, Message> map = new HashMap<String, Message>();

    public String value() {
        throw new RuntimeException("Not a value");
    }

    public Structure put(String key, Message message) {
        map.put(key, message);
        return this;
    }

    public Set<String> keys() {
        return map.keySet();
    }

    public Message read(String key) {
        return map.get(key);
    }
}
