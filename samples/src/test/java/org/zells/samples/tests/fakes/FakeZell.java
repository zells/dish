package org.zells.samples.tests.fakes;

import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;

import java.util.ArrayList;
import java.util.List;

public class FakeZell implements Zell {

    public List<Message> received = new ArrayList<Message>();
    public Address address;

    public Message addressMessage() {
        return new AddressMessage(address);
    }

    @Override
    public void receive(Message message) {
        received.add(message);
    }

    public boolean received(Message message) {
        return received.contains(message);
    }
}
