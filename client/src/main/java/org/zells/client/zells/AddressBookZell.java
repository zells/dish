package org.zells.client.zells;

import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.util.HashMap;
import java.util.Map;

public class AddressBookZell implements Zell {

    private Map<String, Address> addresses = new HashMap<String, Address>();

    @Override
    public void receive(Message message) {
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

    private boolean has(String name) {
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
