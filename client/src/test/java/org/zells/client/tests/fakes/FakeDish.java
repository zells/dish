package org.zells.client.tests.fakes;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.util.Uuid;
import org.zells.dish.util.UuidGenerator;

import java.util.*;

public class FakeDish extends Dish {

    public static String nextAddress;
    public List<Map.Entry<Address, Message>> sent = new ArrayList<Map.Entry<Address, Message>>();
    public List<String> joined = new ArrayList<String>();
    public List<String> connected = new ArrayList<String>();

    public FakeDish() {
        super(null, new FakeUuidGenerator(), null, null);
    }

    @Override
    public void send(Address receiver, Message message) {
        sent.add(new AbstractMap.SimpleEntry<Address, Message>(receiver, message));
        super.send(receiver, message);
    }

    @Override
    public Address add(Zell zell) {
        return super.add(zell);
    }

    @Override
    public void join(String connectionDescription) {
        joined.add(connectionDescription);
    }

    @Override
    public void connect(String connectionDescription) {
        connected.add(connectionDescription);
    }

    private static class FakeUuidGenerator implements UuidGenerator {
        int count = 0;
        @Override
        public Uuid generate() {
            String address = "a" + count;
            if (nextAddress != null) {
                address = nextAddress;
                nextAddress = null;
            }
            return Uuid.fromString(address);
        }
    }
}
