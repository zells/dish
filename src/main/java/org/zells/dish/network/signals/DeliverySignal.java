package org.zells.dish.network.signals;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.Signal;

public class DeliverySignal implements Signal {

    private Delivery delivery;

    public DeliverySignal(Delivery delivery) {
        this.delivery = delivery;
    }

    public Delivery getDelivery() {
        return delivery;
    }
}
