package org.zells.dish.delivery;

import org.zells.dish.util.Uuid;

public class Address {

    private Uuid uuid;

    public Address(Uuid uuid) {
        this.uuid = uuid;
    }

    public static Address fromString(String string) {
        return new Address(Uuid.fromString(string));
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public static Address fromBytes(byte[] bytes) {
        return new Address(new Uuid(bytes));
    }

    public byte[] toBytes() {
        return uuid.getBytes();
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Address
                && uuid.equals(((Address) obj).uuid);
    }
}
