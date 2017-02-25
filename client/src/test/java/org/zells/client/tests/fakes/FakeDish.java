package org.zells.client.tests.fakes;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.Connection;
import org.zells.dish.util.Uuid;
import org.zells.dish.util.UuidGenerator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeDish extends Dish {

    public static String nextAddress;

    public List<Map.Entry<Address, Message>> sent = new ArrayList<Map.Entry<Address, Message>>();
    public Message lastMessage;

    public List<Connection> joined = new ArrayList<Connection>();
    public List<Connection> left = new ArrayList<Connection>();

    public boolean leftAll = false;

    public FakeDish() {
        super(new FakeUuidGenerator(), null);
    }

    @Override
    public void leaveAll() {
        leftAll = true;
    }

    @Override
    public void send(Address receiver, Message message) {
        sent.add(new AbstractMap.SimpleEntry<Address, Message>(receiver, message));
        lastMessage = message;
        super.send(receiver, message);
    }

    @Override
    public Address add(Zell zell) {
        return super.add(zell);
    }

    @Override
    public void join(Connection connection) {
        joined.add(connection);
    }

    @Override
    public void leave(Connection connection) {
        left.add(connection);
    }

    private static class FakeUuidGenerator implements UuidGenerator {
        int count = 0;
        @Override
        public Uuid generate() {
            count++;
            String address = "a" + count;
            if (nextAddress != null) {
                address = nextAddress;
                nextAddress = null;
            }
            return Uuid.fromString(address);
        }
    }
}
