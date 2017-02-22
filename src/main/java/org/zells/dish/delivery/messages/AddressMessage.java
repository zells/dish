package org.zells.dish.delivery.messages;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

public class AddressMessage extends Message {

    private Address value;

    public AddressMessage(Address value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value.toString();
    }

    @Override
    public Address asAddress() {
        return value;
    }

    @Override
    public byte[] asBinary() {
        return value.toBytes();
    }
}
