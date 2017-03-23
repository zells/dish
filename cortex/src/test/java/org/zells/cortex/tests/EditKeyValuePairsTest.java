package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.cortex.synapses.keyvalue.KeyValueEditor;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.NullMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.*;

public class EditKeyValuePairsTest {

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
    public void startObserving() {
        final List<Message> received = new ArrayList<Message>();
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        new KeyValueEditor(target, dish);

        assert received.size() == 2;
        assert received.get(0).read(0).equals(new StringMessage("observers"));
        assert received.get(0).read("add") instanceof AddressMessage;

        assert received.get(1).read(0).equals(new StringMessage("entries"));
        assert received.get(1).read("tell") instanceof AddressMessage;
    }

    @Test
    public void getNewState() {
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                if (message.read(0).equals(new StringMessage("entries"))) {
                    dish.send(message.read("tell").asAddress(), new CompositeMessage()
                            .put("foo", new StringMessage("bar")));
                } else {
                    dish.send(message.read("add").asAddress(),
                            new CompositeMessage(new StringMessage("observer"))
                                    .put("stateChanged", new CompositeMessage()));
                    dish.send(message.read("add").asAddress(),
                            new CompositeMessage(new StringMessage("observer")));
                }
            }
        });

        final List<Map<String, Message>> updated = new ArrayList<Map<String, Message>>();
        new KeyValueEditor(target, dish) {
            protected void onUpdate(Map<String, Message> entries) {
                updated.add(entries);
            }
        };

        assert updated.size() == 2;
        assert updated.get(0).keySet().equals(new HashSet<String>(Collections.singletonList("foo")));
        assert updated.get(0).get("foo").equals(new StringMessage("bar"));
    }

    @Test
    public void addEntry() {
        final List<Message> received = new ArrayList<Message>();
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        new KeyValueEditor(target, dish).put("foo", new NullMessage());

        assert received.contains(new CompositeMessage(new StringMessage("entries"))
                .put("at", new StringMessage("foo"))
                .put("put", new NullMessage()));
    }

    @Test
    public void removeEntry() {
        final List<Message> received = new ArrayList<Message>();
        Address target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        new KeyValueEditor(target, dish).remove("foo");

        assert received.contains(new CompositeMessage(new StringMessage("entries"))
                .put("remove", new StringMessage("foo")));
    }
}
