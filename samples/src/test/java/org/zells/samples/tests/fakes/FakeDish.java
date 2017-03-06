package org.zells.samples.tests.fakes;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeDish extends Dish {

    public List<Map.Entry<Address, Message>> sent = new ArrayList<Map.Entry<Address, Message>>();

    public FakeDish() {
        super(new BasicUuidGenerator(), new EncodingRepository());
    }

    @Override
    public Messenger send(Address receiver, Message message) {
        sent.add(new AbstractMap.SimpleEntry<Address, Message>(receiver, message));
        return new Messenger(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
