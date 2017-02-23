package org.zells.client.tests.fakes;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.util.*;

public class FakeDish extends Dish {

    public String nextAddress = "dada";
    public List<Map.Entry<Address, Message>> sent = new ArrayList<Map.Entry<Address, Message>>();

    public FakeDish() {
        super(null, null, null, null);
    }

    @Override
    public Dish start() {
        return this;
    }

    @Override
    public void send(Address receiver, Message message) {
        sent.add(new AbstractMap.SimpleEntry<Address, Message>(receiver, message));
    }

    @Override
    public Address add(Zell zell) {
        return Address.fromString(nextAddress);
    }

    @Override
    public void join(String connectionDescription) {
    }
}
