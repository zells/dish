package org.zells.dish;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.fakes.FakeServer;
import org.zells.dish.fakes.FakeUuidGenerator;
import org.zells.dish.fakes.FakeZell;
import org.zells.dish.delivery.messages.Structure;
import org.zells.dish.delivery.messages.Value;

public class DeliverMessagesTest {

    private Dish dish;

    @Before
    public void setUp() {
        dish = new Dish(new FakeServer(), new FakeUuidGenerator(), new EncodingRepository());
    }

    @Test(expected = ReceiverNotFoundException.class)
    public void failIfZellDoesNotExist() {
        dish.send(new Address("not an address"), new Value("a value"));
    }

    @Test
    public void deliverValue() {
        FakeZell aZell = new FakeZell();

        Address anAddress = dish.add(aZell);
        dish.send(anAddress, new Value("a value"));

        assert aZell.received.value().equals("a value");
    }

    @Test
    public void deliverStructure() {
        FakeZell aZell = new FakeZell();

        Address anAddress = dish.add(aZell);
        dish.send(anAddress, new Structure()
                .put("one", new Value("uno"))
                .put("and", new Structure()
                        .put("two", new Value("dos"))));


        assert aZell.received.keys().size() == 2;
        assert aZell.received.keys().contains("one");
        assert aZell.received.keys().contains("and");

        assert aZell.received.read("one").value().equals("uno");
        assert aZell.received.read("and").read("two").value().equals("dos");
    }

    @Test
    public void haveUniqueAddresses() {
        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = dish.add(zellOne);
        Address addressTwo = dish.add(zellTwo);

        dish.send(addressOne, new Value("for one"));
        dish.send(addressTwo, new Value("for two"));

        assert zellOne.received.value().equals("for one");
        assert zellTwo.received.value().equals("for two");
    }
}
