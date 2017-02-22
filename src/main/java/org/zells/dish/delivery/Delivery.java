package org.zells.dish.delivery;

public class Delivery {

    private final Address receiver;
    private final Message message;
    private String uuid;

    public Delivery(Address receiver, Message message, String uuid) {
        this.receiver = receiver;
        this.message = message;
        this.uuid = uuid;
    }

    public Address getReceiver() {
        return receiver;
    }

    public Message getMessage() {
        return message;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Delivery
                && uuid.equals(((Delivery) obj).uuid)
                && receiver.equals(((Delivery) obj).receiver)
                && message.equals(((Delivery) obj).message);
    }
}
