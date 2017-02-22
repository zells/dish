package org.zells.dish.delivery;

public class Address {

    private String value;

    public Address(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Address
                && value.equals(((Address) obj).value);
    }

    public static Address parse(String string) {
        return new Address(string);
    }

    public byte[] getBytes() {
        return value.getBytes();
    }

    public String asString() {
        return value;
    }

    public static Address fromBytes(byte[] bytes) {
        return new Address(new String(bytes));
    }
}
