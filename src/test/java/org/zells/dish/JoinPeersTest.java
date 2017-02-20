package org.zells.dish;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.fakes.*;
import org.zells.dish.delivery.messages.Value;
import org.zells.dish.network.Connection;

import java.util.IdentityHashMap;
import java.util.Map;

public class JoinPeersTest {

    private Map<Dish, Connection> connections = new IdentityHashMap<Dish, Connection>();
    private Dish dishOne;
    private Dish dishTwo;
    private Dish dishThree;

    @Before
    public void setUp() {
        dishOne = newDish();
        dishTwo = newDish();
        dishThree = newDish();
    }

    @Test
    public void deliverToPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishTwo.add(aZell);

        dishOne.join(connections.get(dishTwo));
        dishOne.send(anAddress, new Value("a value"));

        assert aZell.received.value().equals("a value");
    }

    @Test
    public void forwardDelivery() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join(connections.get(dishTwo));
        dishTwo.join(connections.get(dishThree));

        dishOne.send(anAddress, new Value("a value"));

        assert aZell.received.value().equals("a value");
    }

    @Test
    public void searchInAllPeers() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishThree.add(aZell);

        dishOne.join(connections.get(dishTwo));
        dishOne.join(connections.get(dishThree));

        dishOne.send(anAddress, new Value("a value"));

        assert aZell.received.value().equals("a value");
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void avoidLoops() {
        dishOne.join(connections.get(dishTwo));
        dishTwo.join(connections.get(dishThree));
        dishThree.join(connections.get(dishOne));

        dishOne.send(new Address("loop"), new Value("value"));
    }

    @Test
    public void joinPeer() {
        FakeZell aZell = new FakeZell();
        Address anAddress = dishOne.add(aZell);

        dishOne.join(connections.get(dishTwo));
        dishTwo.send(anAddress, new Value("a value"));

        assert aZell.received.value().equals("a value");
    }

    private Dish newDish() {
        FakeUuidGenerator generator = new FakeUuidGenerator();
        EncodingRepository encodings = new EncodingRepository();
        encodings.add(new FakeEncoding());

        FakeServer server = new FakeServer();
        Dish dish = new Dish(server, generator, encodings);

        connections.put(dish, server.getConnection());

        return dish;
    }
}
