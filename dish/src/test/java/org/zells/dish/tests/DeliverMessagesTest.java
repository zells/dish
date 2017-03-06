package org.zells.dish.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.*;
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
    private FakeUuidGenerator generator;
    private EncodingRepository encodings;

    @Before
    public void setUp() {
        generator = new FakeUuidGenerator();
        encodings = new EncodingRepository().add(new FakeEncoding());

        dishOne = new Dish(generator, encodings);
        dishTwo = new Dish(generator, encodings);
        dishThree = new Dish(generator, encodings);
    }

    @Test
    public void receiverDoesNotExist() {
        dishOne.send(Address.fromString("not an address"), new StringMessage("a string"))
                .when(new Messenger.Failed() {
                    public void then(Exception e) {
                        assert e instanceof ReceiverNotFoundException;
                    }
                })
                .sync();
    }

    @Test
    public void deliverLocally() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishOne.add(aZell);

        dishOne.send(anAddress, new StringMessage("a string")).sync();

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void exceptionWhileReceiving() {
        final Exception[] logged = new Exception[1];
        Dish dish = new Dish(generator, encodings) {
            @Override
            protected void logError(Exception e, Delivery delivery) {
                logged[0] = e;
            }
        };

        Address anAddress = dish.add(new Zell() {
            public void receive(Message message) {
                throw new RuntimeException("Nope");
            }
        });

        dish.send(anAddress, new StringMessage("a string")).sync();
        assert logged[0].getMessage().equals("Nope");
    }

    @Test
    public void haveUniqueAddresses() {
        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = dishOne.add(zellOne);
        Address addressTwo = dishOne.add(zellTwo);

        dishOne.send(addressOne, new StringMessage("for one")).sync();
        dishOne.send(addressTwo, new StringMessage("for two")).sync();

        assert zellOne.received.asString().equals("for one");
        assert zellTwo.received.asString().equals("for two");
    }

    @Test
    public void deliverToPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishTwo.add(aZell);

        dishOne.join(connect(dishOne, dishTwo));
        dishOne.send(anAddress, new StringMessage("a string")).sync();

        assert aZell.received.asString().equals("a string");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join(connect(dishOne, dishTwo));
        dishTwo.join(connect(dishTwo, dishThree));

        dishOne.send(anAddress, new StringMessage("a string")).sync();

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

        dishOne.send(addressTwo, new StringMessage("two")).sync();
        dishOne.send(addressThree, new StringMessage("three")).sync();

        assert zellTwo.received.asString().equals("two");
        assert zellThree.received.asString().equals("three");
    }

    @Test
    public void avoidLoops() {
        dishOne.join(connect(dishOne, dishTwo));
        dishTwo.join(connect(dishTwo, dishThree));
        dishThree.join(connect(dishThree, dishOne));

        final boolean[] failed = new boolean[1];
        dishOne.send(Address.fromString("loop"), new StringMessage("a string"))
                .when(new Messenger.Failed() {
                    @Override
                    public void then(Exception e) {
                        failed[0] = true;
                    }
                })
                .sync();

        assert failed[0];
    }

    @Test
    public void joinBack() {
        FakeZell zellOne = new FakeZell();
        Address addressOne = dishOne.add(zellOne);
        FakeZell zellTwo = new FakeZell();
        Address addressTwo = dishTwo.add(zellTwo);

        dishOne.join(connect(dishOne, dishTwo));

        dishOne.send(addressTwo, new StringMessage("two")).sync();
        dishTwo.send(addressOne, new StringMessage("one")).sync();

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

        assertFails(dishTwo.send(addressOne, new StringMessage("a string")));
        assertFails(dishOne.send(addressTwo, new StringMessage("a string")));
    }

    @Test
    public void leaveAll() {
        final Address addressOne = dishOne.add(new FakeZell());
        final Address addressTwo = dishTwo.add(new FakeZell());
        final Address addressThree = dishThree.add(new FakeZell());

        dishOne.join(connect(dishOne, dishTwo));
        dishOne.join(connect(dishOne, dishThree));
        dishOne.leaveAll();

        assertFails(dishOne.send(addressTwo, new StringMessage("a string")));
        assertFails(dishOne.send(addressThree, new StringMessage("a string")));
        assertFails(dishTwo.send(addressOne, new StringMessage("a string")));
    }

    @Test
    public void slowReaction() throws InterruptedException {
        Address anAddress = dishOne.add(new FakeZell());

        Messenger messenger = dishOne.send(anAddress, new StringMessage("a string"));
        Thread.sleep(20);
        assertSucceeds(messenger);
    }

    @Test
    public void slowCatcher() throws InterruptedException {
        Messenger messenger = dishOne.send(Address.fromString("aa"), new StringMessage("a string"));
        Thread.sleep(20);
        assertFails(messenger);
    }

    private void assertSucceeds(Messenger messenger) {
        final boolean[] delivered = {false};
        messenger.when(new Messenger.Delivered() {
            public void then() {
                delivered[0] = true;
            }
        }).sync();
        assert delivered[0];
    }

    private void assertFails(Messenger messenger) {
        final boolean[] failed = {false};
        messenger.when(new Messenger.Failed() {
            public void then(Exception e) {
                failed[0] = true;
            }
        }).sync();
        assert failed[0];
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
}
