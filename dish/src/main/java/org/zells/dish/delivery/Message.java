package org.zells.dish.delivery;

import org.zells.dish.delivery.messages.NullMessage;

import java.util.HashSet;
import java.util.Set;

public abstract class Message {

    public boolean isNull() {
        return false;
    }

    public String asString() {
        return "";
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
        return Address.fromBytes(new byte[0]);
    }

    public Set<String> keys() {
        return new HashSet<String>();
    }

    public Message read(String key) {
        return new NullMessage();
    }

    public Message read(int key) {
        return read(Integer.toString(key));
    }

    @Override
    public String toString() {
        return asString();
    }
}
