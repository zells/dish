package org.zells.cortex.synapses.keyvalue;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.HashMap;
import java.util.Map;

public class KeyValueEditor {

    private Address target;
    private Dish dish;
    private final Address updater;

    public KeyValueEditor(Address target, Dish dish) {
        this.target = target;
        this.dish = dish;

        updater = dish.add(new UpdatedZell());
        Address me = dish.add(new ReceiverZell());
        dish.send(target, new CompositeMessage()
                .put(0, new StringMessage("observers"))
                .put("put", new AddressMessage(me)));

    }

    protected void onUpdate(Map<String, Message> entries) {
    }

    public void put(String key, Message value) {
        dish.send(target, new CompositeMessage(new StringMessage("entries"))
                .put("at", new StringMessage(key))
                .put("put", value));
    }

    public void remove(String key) {
        dish.send(target, new CompositeMessage(new StringMessage("entries"))
                .put("remove", new StringMessage(key)));
    }

    private class ReceiverZell implements Zell {
        @Override
        public void receive(Message message) {
            if (message.read(0).equals(new StringMessage("observer")) && !message.read("stateChanged").isNull()) {
                dish.send(target, new CompositeMessage()
                        .put(0, new StringMessage("tellEntries"))
                        .put("to", new AddressMessage(updater)));
            }
        }
    }

    private class UpdatedZell implements Zell {
        @Override
        public void receive(Message message) {
            Map<String, Message> entries = new HashMap<String, Message>();
            for (String key : message.keys()) {
                entries.put(key, message.read(key));
            }
            onUpdate(entries);
        }
    }
}
