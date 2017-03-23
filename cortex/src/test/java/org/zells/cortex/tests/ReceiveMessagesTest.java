package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.cortex.zells.ReceiverZell;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceiveMessagesTest {

    private Dish dish;

    @Before
    public void setUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository()) {
            @Override
            public Messenger send(Address receiver, Message message) {
                return super.send(receiver, message).sync();
            }
        };
    }

    @Test
    public void subscribe() {
        final List<Message> received = new ArrayList<Message>();
        Address subscriber = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });

        ReceiverZell receiver = new ReceiverZell(dish) {
            @Override
            protected String getTimeAsIsoString() {
                return "2011-12-13T14:15:16Z";
            }
        };
        receiver.receive(new CompositeMessage().put("subscribe", new AddressMessage(subscriber)));
        receiver.receive(new StringMessage("foo"));

        assert received.equals(Collections.singletonList(
                new CompositeMessage()
                        .put("sequence", new IntegerMessage(1))
                        .put("message", new StringMessage("foo"))
                        .put("time", new StringMessage("2011-12-13T14:15:16Z"))
        ));
    }

    @Test
    public void unsubscribe() {
        final List<Message> received = new ArrayList<Message>();
        Address subscriber = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });

        ReceiverZell receiver = new ReceiverZell(dish);
        receiver.receive(new CompositeMessage().put("subscribe", new AddressMessage(subscriber)));
        receiver.receive(new StringMessage("foo"));
        receiver.receive(new CompositeMessage().put("unsubscribe", new AddressMessage(subscriber)));
        receiver.receive(new StringMessage("bar"));

        assert received.size() == 1;
        assert received.get(0).read("message").equals(new StringMessage("foo"));
    }

    @Test
    public void severalSubscribers() {
        final List<Message> received1 = new ArrayList<Message>();
        Address subscriber1 = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received1.add(message);
            }
        });
        final List<Message> received2 = new ArrayList<Message>();
        Address subscriber2 = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received2.add(message);
            }
        });

        ReceiverZell receiver = new ReceiverZell(dish);
        receiver.receive(new CompositeMessage().put("subscribe", new AddressMessage(subscriber1)));
        receiver.receive(new CompositeMessage().put("subscribe", new AddressMessage(subscriber2)));

        receiver.receive(new StringMessage("foo"));

        assert received1.size() == 1;
        assert received2.size() == 1;
    }

    @Test
    public void forgetMessages() {
        final List<Message> received = new ArrayList<Message>();
        Address subscriber = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });

        ReceiverZell receiver = new ReceiverZell(dish);
        receiver.receive(new StringMessage("one"));
        receiver.receive(new StringMessage("two"));
        receiver.receive(new CompositeMessage().put("subscribe", new AddressMessage(subscriber)));
        receiver.receive(new StringMessage("three"));

        assert received.size() == 1;
        assert received.get(0).read("message").equals(new StringMessage("three"));
    }
}
