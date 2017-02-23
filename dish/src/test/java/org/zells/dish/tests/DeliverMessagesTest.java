package org.zells.dish.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.Connection;
import org.zells.dish.network.ConnectionFactory;
import org.zells.dish.network.ConnectionRepository;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.tests.fakes.*;

public class DeliverMessagesTest {

    private Dish dish;
    private Dish dishTwo;
    private Dish dishThree;
    private FakeUuidGenerator uuidGenerator;
    private EncodingRepository encodings;
    private ConnectionRepository connections;

    @Before
    public void setUp() {
        uuidGenerator = new FakeUuidGenerator();
        connections = new ConnectionRepository();
        encodings = new EncodingRepository()
                .add(new FakeEncoding());

        dish = newDish(1);
        dishTwo = newDish(2);
        dishThree = newDish(3);
    }

    private Dish newDish(final int id) {
        final FakeServer server = new FakeServer(id);
        connections.add(new ConnectionFactory() {
            public boolean canBuild(String description) {
                return description.equals("fake:" + id);
            }

            public Connection build(String description) {
                return new FakeConnection(server);
            }
        });

        return new Dish(server, uuidGenerator, encodings, connections);
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void failIfZellDoesNotExist() {
        dish.send(Address.fromString("not an address"), new StringMessage("a asString"));
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

        dish.join("fake:2");
        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dish.join("fake:2");
        dishTwo.join("fake:3");

        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test
    public void searchInAllPeers() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dish.join("fake:2");
        dish.join("fake:3");

        dish.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void avoidLoops() {
        dish.join("fake:2");
        dishTwo.join("fake:3");
        dishThree.join("fake:1");

        dish.send(Address.fromString("loop"), new StringMessage("asString"));
    }

    @Test
    public void joinPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dish.add(aZell);

        dish.join("fake:2");
        dishTwo.send(anAddress, new StringMessage("a asString"));

        assert aZell.received.asString().equals("a asString");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void leavePeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dish.add(aZell);

        dish.join("fake:2");
        dish.leave("fake:2");
        dishTwo.send(anAddress, new StringMessage("a asString"));
    }
}
