package org.zells.client.synapses.communicator;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

abstract class ReceiverZell implements Zell {
    private Dish dish;
    private Address address;

    ReceiverZell(Dish dish) {
        this.dish = dish;
    }

    protected abstract void received(Message message);

    @Override
    public void receive(Message message) {
        dish.remove(address);
        received(message);
    }

    void setAddress(Address address) {
        this.address = address;
    }
}
