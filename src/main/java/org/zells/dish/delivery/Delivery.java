package org.zells.dish.delivery;

import org.zells.dish.util.Uuid;

public class Delivery {

    private final Address receiver;
    private final Message message;
    private Uuid uuid;

    public Delivery(Uuid uuid, Address receiver, Message message) {
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

    public Uuid getUuid() {
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
