package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.cortex.synapses.listener.Listener;
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

public class ListenToMessages {

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
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        new Listener(target, dish);

        assert received.get(0).read("subscribe") instanceof AddressMessage;
    }

    @Test
    public void receiveMessage() {
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read("subscribe").asAddress(),
                        new CompositeMessage()
                                .put("sequence", new IntegerMessage(1))
                                .put("time", new StringMessage("2011-12-13T14:15:16Z"))
                                .put("message", new StringMessage("foo")));
            }
        });

        final List<Listener.ReceivedMessage> received = new ArrayList<Listener.ReceivedMessage>();
        new Listener(target, dish) {
            protected void onReceive(Listener.ReceivedMessage message) {
                assert message.getSequence() == 1;
                assert message.getMessage().equals(new StringMessage("foo"));
                assert message.getTimeAsIsoString().equals("2011-12-13T14:15:16Z");
                received.add(message);
            }
        };

        assert received.equals(Collections.singletonList(
                new Listener.ReceivedMessage(1, new StringMessage("foo"),"2011-12-13T14:15:16Z")));
    }

    @Test
    public void receiveOutOfSequence() {
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read("subscribe").asAddress(),
                        new CompositeMessage()
                                .put("sequence", new IntegerMessage(42))
                                .put("message", new StringMessage("two")));
                dish.send(message.read("subscribe").asAddress(),
                        new CompositeMessage()
                                .put("sequence", new IntegerMessage(7))
                                .put("message", new StringMessage("one")));
            }
        });

        Listener listener = new Listener(target, dish);

        assert listener.getReceivedMessages().get(0).getMessage().equals(new StringMessage("one"));
        assert listener.getReceivedMessages().get(1).getMessage().equals(new StringMessage("two"));
    }
}
