package org.zells.dish.delivery;

public class ReceiverNotFoundException extends RuntimeException {

    public ReceiverNotFoundException(Delivery delivery) {
        super("Could not find " + delivery.getReceiver().toString());
    }
}
