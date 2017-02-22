package org.zells.dish;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.fakes.FakeEncoding;
import org.zells.dish.fakes.FakeServer;
import org.zells.dish.fakes.FakeUuidGenerator;
import org.zells.dish.fakes.FakeZell;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.Connection;
import org.zells.dish.network.encoding.EncodingRepository;

import java.util.IdentityHashMap;
import java.util.Map;

public class DeliverMessagesTest {

    private FakeDish dish;
    private FakeDish dishTwo;
    private FakeDish dishThree;

    @Before
    public void setUp() {
        dish = new FakeDish();
        dishTwo = new FakeDish();
        dishThree = new FakeDish();
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void failIfZellDoesNotExist() {
        dish.send(new Address("not an address"), new StringMessage("a asString"));
    }

    @Test
    public void deliverLocally() {
        FakeZell aZell = new FakeZell();

        Address anAddress = dish.add(aZell);
        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test
    public void haveUniqueAddresses() {
        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = dish.add(zellOne);
        Address addressTwo = dish.add(zellTwo);

        dish.send(addressOne, new StringMessage("for one"));
        dish.send(addressTwo, new StringMessage("for two"));

        assert zellOne.received.asString().equals("for one");
        assert zellTwo.received.asString().equals("for two");
    }

    @Test
    public void deliverToPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishTwo.add(aZell);

        dish.join(dishTwo.connection);
        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dish.join(dishTwo.connection);
        dishTwo.join(dishThree.connection);

        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test
    public void searchInAllPeers() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dish.join(dishTwo.connection);
        dish.join(dishThree.connection);

        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void avoidLoops() {
        dish.join(dishTwo.connection);
        dishTwo.join(dishThree.connection);
        dishThree.join(dish.connection);

        dish.send(new Address("loop"), new StringMessage("asString"));
    }

    @Test
    public void joinPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dish.add(aZell);

        dish.join(dishTwo.connection);
        dishTwo.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    static class FakeDish extends Dish {

        final Connection connection;

        private static int lastId = 0;
        private static Map<Integer, Connection> connections = new IdentityHashMap<Integer, Connection>();

        FakeDish() {
            super(server(), new FakeUuidGenerator(), encodings());
            connection = connections.get(lastId);
            lastId++;
        }

        private static FakeServer server() {
            FakeServer server = new FakeServer();
            connections.put(lastId, server.getConnection());
            return server;
        }

        private static EncodingRepository encodings() {
            EncodingRepository repository = new EncodingRepository();
            repository.add(new FakeEncoding());
            return repository;
        }
    }
}
