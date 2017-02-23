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

    private Dish dishOne;
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

        dishOne = newDish(1);
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
        dishOne.send(Address.fromString("not an address"), new StringMessage("a string"));
    }

    @Test
    public void deliverLocally() {
        FakeZell aZell = new FakeZell();

        Address anAddress = dishOne.add(aZell);
        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void haveUniqueAddresses() {
        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = dishOne.add(zellOne);
        Address addressTwo = dishOne.add(zellTwo);

        dishOne.send(addressOne, new StringMessage("for one"));
        dishOne.send(addressTwo, new StringMessage("for two"));

        assert zellOne.received.asString().equals("for one");
        assert zellTwo.received.asString().equals("for two");
    }

    @Test
    public void deliverToPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishTwo.add(aZell);

        dishOne.join("fake:2");
        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join("fake:2");
        dishTwo.join("fake:3");

        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void searchInAllPeers() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join("fake:2");
        dishOne.join("fake:3");

        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void avoidLoops() {
        dishOne.join("fake:2");
        dishTwo.join("fake:3");
        dishThree.join("fake:1");

        dishOne.send(Address.fromString("loop"), new StringMessage("a string"));
    }

    @Test
    public void joinPeer() {
        FakeZell zellOne = new FakeZell();
        Address addressOne = dishOne.add(zellOne);
        FakeZell zellTwo = new FakeZell();
        Address addressTwo = dishTwo.add(zellTwo);

        dishOne.join("fake:2");

        dishTwo.send(addressOne, new StringMessage("one"));
        dishOne.send(addressTwo, new StringMessage("two"));

        assert zellOne.received.asString().equals("one");
        assert zellTwo.received.asString().equals("two");
    }

    @Test
    public void leavePeer() {
        final Address addressOne = dishOne.add(new FakeZell());
        final Address addressTwo = dishTwo.add(new FakeZell());

        dishOne.join("fake:2");
        dishOne.leave("fake:2");

        assertFails(new Runnable() {
            public void run() {
                dishTwo.send(addressOne, new StringMessage("a string"));
            }
        });
        assertFails(new Runnable() {
            public void run() {
                dishOne.send(addressTwo, new StringMessage("a string"));
            }
        });
    }

    @Test
    public void leaveAll() {
        final Address addressTwo = dishTwo.add(new FakeZell());
        final Address addressThree = dishThree.add(new FakeZell());

        dishOne.join("fake:2");
        dishOne.join("fake:3");
        dishOne.stop();

        assertFails(new Runnable() {
            public void run() {
                dishOne.send(addressTwo, new StringMessage("a string"));
            }
        });
        assertFails(new Runnable() {
            public void run() {
                dishOne.send(addressThree, new StringMessage("a string"));
            }
        });
    }

    private void assertFails(Runnable doomed) {
        try {
            doomed.run();
        } catch (Exception e) {
            return;
        }
        throw new RuntimeException("Did not catch anything");
    }
}
