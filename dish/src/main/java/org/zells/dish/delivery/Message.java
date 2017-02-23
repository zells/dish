package org.zells.dish.delivery;

import java.util.HashSet;
import java.util.Set;

public abstract class Message {

    public String asString() {
        return null;
    }

    public boolean isNull() {
        return asString() == null;
    }

    public boolean isTrue() {
        return !isNull() && !asString().equals("");
    }

    public int asInteger() {
        return isTrue() ? 1 : 0;
    }

    public byte[] asBytes() {
        return isNull() ? new byte[0] : (isTrue() ? new byte[]{1} : new byte[]{0});
    }

    public Address asAddress() {
        throw new RuntimeException("not an address");
    }

    public Set<String> keys() {
        return new HashSet<String>();
    }

    public Message read(String key) {
        throw new RuntimeException("no value for [" + key + "]");
    }

    public Message read(int key) {
        return read(Integer.toString(key));
    }

    @Override
    public String toString() {
        return asString();
    }
}
