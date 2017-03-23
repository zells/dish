package org.zells.cortex.synapses.keyvalue;

import org.zells.cortex.SynapseModel;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.HashMap;
import java.util.Map;

public class KeyValueEditor extends SynapseModel {

    private Address updater;

    public KeyValueEditor(Address target, Dish dish) {
        super(target, dish);
    }

    @Override
    protected void start() {
        updater = add(new UpdatedZell());
        Address me = add(new ReceiverZell());

        send(new CompositeMessage()
                .put(0, new StringMessage("observers"))
                .put("add", new AddressMessage(me)));
        getEntries();
    }

    protected void onUpdate(Map<String, Message> entries) {
    }

    private void getEntries() {
        send(new CompositeMessage()
                .put(0, new StringMessage("entries"))
                .put("tell", new AddressMessage(updater)));
    }

    public void put(String key, Message value) {
        send(new CompositeMessage(new StringMessage("entries"))
                .put("at", new StringMessage(key))
                .put("put", value));
    }

    public void remove(String key) {
        send(new CompositeMessage(new StringMessage("entries"))
                .put("remove", new StringMessage(key)));
    }

    private class ReceiverZell implements Zell {
        @Override
        public void receive(Message message) {
            if (message.read(0).equals(new StringMessage("observer")) && !message.read("stateChanged").isNull()) {
                getEntries();
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
