package org.zells.cortex.zells;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddressBookZell implements Zell {

    private Map<String, Address> addresses = new HashMap<String, Address>();
    private Dish dish;
    private Set<Address> observers = new HashSet<Address>();

    public AddressBookZell(Dish dish) {
        this.dish = dish;
    }

    @Override
    public void receive(Message message) {
        if (!message.read("use").isNull() && !message.read("for").isNull()) {
            String name = message.read("use").asString().replace(" ", "");
            Address address = message.read("for").asAddress();
            boolean replaced = addresses.containsKey(name);
            addresses.put(name, address);
            notifyObservers(new CompositeMessage()
                    .put(replaced ? "replaced" : "added", new CompositeMessage()
                            .put(name, new AddressMessage(address))));
        } else if (!message.read("forget").isNull()) {
            String name = message.read("forget").asString();
            Address removed = addresses.remove(name);
            if (removed != null) {
                notifyObservers(new CompositeMessage()
                        .put("removed", new CompositeMessage()
                                .put(name, new AddressMessage(removed))));
            }
        } else if (message.read(0).asString().equals("tellEntries") && !message.read("to").isNull()) {
            CompositeMessage book = new CompositeMessage();
            for (String name : addresses.keySet()) {
                book.put(name, new AddressMessage(addresses.get(name)));
            }
            dish.send(message.read("to").asAddress(), book);
        } else if (message.read(0).equals(new StringMessage("observers"))) {
            if (!message.read("add").isNull()) {
                observers.add(message.read("add").asAddress());
            }
        }
    }

    private void notifyObservers(Message change) {
        for (Address observer : observers) {
            dish.send(observer, new CompositeMessage(new StringMessage("observer"))
                    .put("stateChanged", change));
        }
    }

    public void put(String name, Address address) {
        addresses.put(name, address);
    }

    public Address get(String name) {
        if (!has(name)) {
            throw new RuntimeException("Not in address book: " + name);
        }
        return addresses.get(name);
    }

    public boolean has(String name) {
        return addresses.containsKey(name);
    }

    public Map<String, Address> getAddresses() {
        return new HashMap<String, Address>(addresses);
    }

    public boolean contains(Address address) {
        return addresses.containsValue(address);
    }

    public String nameOf(Address address) {
        for (String name : addresses.keySet()) {
            if (addresses.get(name).equals(address)) {
                return name;
            }
        }
        throw new RuntimeException("Not in book: " + address);
    }
}
