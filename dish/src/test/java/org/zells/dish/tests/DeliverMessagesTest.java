package org.zells.dish.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.tests.fakes.FakeConnection;
import org.zells.dish.tests.fakes.FakeEncoding;
import org.zells.dish.tests.fakes.FakeUuidGenerator;
import org.zells.dish.tests.fakes.FakeZell;

public class DeliverMessagesTest {

    private Dish dishOne;
    private Dish dishTwo;
    private Dish dishThree;

    @Before
    public void setUp() {
        FakeUuidGenerator generator = new FakeUuidGenerator();
        EncodingRepository encodings = new EncodingRepository().add(new FakeEncoding());

        dishOne = new Dish(generator, encodings);
        dishTwo = new Dish(generator, encodings);
        dishThree = new Dish(generator, encodings);
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

        dishOne.join(connect(dishOne, dishTwo));
        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join(connect(dishOne, dishTwo));
        dishTwo.join(connect(dishTwo, dishThree));

        dishOne.send(anAddress, new StringMessage("a string"));

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void searchInAllPeers() {
        FakeZell zellTwo = new FakeZell();
        Address addressTwo = dishThree.add(zellTwo);
        FakeZell zellThree = new FakeZell();
        Address addressThree = dishThree.add(zellThree);

        dishOne.join(connect(dishOne, dishTwo));
        dishOne.join(connect(dishOne, dishThree));

        dishOne.send(addressTwo, new StringMessage("two"));
        dishOne.send(addressThree, new StringMessage("three"));

        assert zellTwo.received.asString().equals("two");
        assert zellThree.received.asString().equals("three");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void avoidLoops() {
        dishOne.join(connect(dishOne, dishTwo));
        dishTwo.join(connect(dishTwo, dishThree));
        dishThree.join(connect(dishThree, dishOne));

        dishOne.send(Address.fromString("loop"), new StringMessage("a string"));
    }

    @Test
    public void joinBack() {
        FakeZell zellOne = new FakeZell();
        Address addressOne = dishOne.add(zellOne);
        FakeZell zellTwo = new FakeZell();
        Address addressTwo = dishTwo.add(zellTwo);

        dishOne.join(connect(dishOne, dishTwo));

        dishOne.send(addressTwo, new StringMessage("two"));
        dishTwo.send(addressOne, new StringMessage("one"));

        assert zellOne.received.asString().equals("one");
        assert zellTwo.received.asString().equals("two");
    }

    @Test
    public void leavePeer() {
        final Address addressOne = dishOne.add(new FakeZell());
        final Address addressTwo = dishTwo.add(new FakeZell());

        Connection connection = connect(dishOne, dishTwo);
        dishOne.join(connection);
        dishOne.leave(connection);

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
        final Address addressOne = dishOne.add(new FakeZell());
        final Address addressTwo = dishTwo.add(new FakeZell());
        final Address addressThree = dishThree.add(new FakeZell());

        dishOne.join(connect(dishOne, dishTwo));
        dishOne.join(connect(dishOne, dishThree));
        dishOne.leaveAll();

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
        assertFails(new Runnable() {
            public void run() {
                dishTwo.send(addressOne, new StringMessage("a string"));
            }
        });
    }

    private Connection connect(Dish a, Dish b) {
        FakeConnection ab = new FakeConnection();
        FakeConnection ba = new FakeConnection();
        ab.to(ba);
        ba.to(ab);

        b.listen(ab);
        a.listen(ba);

        return ba;
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
