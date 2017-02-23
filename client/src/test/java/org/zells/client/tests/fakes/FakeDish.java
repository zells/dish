package org.zells.client.tests.fakes;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.Server;
import org.zells.dish.network.SignalListener;
import org.zells.dish.util.Uuid;
import org.zells.dish.util.UuidGenerator;

import java.util.*;

public class FakeDish extends Dish {

    public static String nextAddress;
    public List<Map.Entry<Address, Message>> sent = new ArrayList<Map.Entry<Address, Message>>();
    public List<String> joined = new ArrayList<String>();
    public List<String> connected = new ArrayList<String>();

    public FakeDish() {
        super(new FakeServer(), new FakeUuidGenerator(), null, null);
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
            count++;
            String address = "a" + count;
            if (nextAddress != null) {
                address = nextAddress;
                nextAddress = null;
            }
            return Uuid.fromString(address);
        }
    }

    private static class FakeServer implements Server {
        @Override
        public void start(SignalListener listener) {
        }

        @Override
        public void stop() {
        }

        @Override
        public String getConnectionDescription() {
            return null;
        }
    }
}
