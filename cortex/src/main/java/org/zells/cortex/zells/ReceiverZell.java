package org.zells.cortex.zells;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ReceiverZell implements Zell {

    private List<Address> subscribers = new ArrayList<Address>();
    private Dish dish;
    private int sequence = 1;

    public ReceiverZell(Dish dish) {
        this.dish = dish;
    }

    @Override
    public void receive(Message message) {
        if (message.read("subscribe") instanceof AddressMessage) {
            subscribers.add(message.read("subscribe").asAddress());
        } else if (message.read("unsubscribe") instanceof AddressMessage) {
            subscribers.remove(message.read("unsubscribe").asAddress());
        } else {
            for (Address subscriber : subscribers) {
                dish.send(subscriber, new CompositeMessage()
                        .put("sequence", new IntegerMessage(sequence++))
                        .put("time", new StringMessage(getTimeAsIsoString()))
                        .put("message", message));
            }
        }
    }

    protected String getTimeAsIsoString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }
}
