package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.nio.ByteBuffer;

public class AddressMessage extends Message {
    private Address address;

    public AddressMessage(Address address) {
        this.address = address;
    }

    @Override
    public boolean isTrue() {
        return true;
    }

    @Override
    public String asString() {
        return address.toString();
    }

    @Override
    public int asInteger() {
        return ByteBuffer.wrap(address.toBytes()).getShort();
    }

    @Override
    public byte[] asBytes() {
        return address.toBytes();
    }

    @Override
    public Address asAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AddressMessage
                && address.equals(((AddressMessage) obj).address);
    }
}
