package org.zells.cortex.tests;

import org.junit.Before;
import org.zells.cortex.synapses.communicator.Communicator;
import org.zells.cortex.zells.AddressBookZell;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseTest {

    public List<Message> received = new ArrayList<Message>();
    protected List<String> log = new ArrayList<String>();

    public Dish dish;
    protected Address target;

    private Communicator communicator;

    @Before
    public void SetUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository());
        target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        AddressBookZell book = new AddressBookZell(dish);
        communicator = new Communicator(target, dish, book);
    }

    protected void send(String input) {
        send(input, new Listener());
    }

    protected void send(String input, Listener listener) {
        try {
            communicator.send(input, listener);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected class Listener extends Communicator.Listener {

        protected void onSending(Messenger messenger) {
            log.add("sending");
            messenger.sync(2);
        }

        protected void onParsed(String receiver, Message message) {
            log.add("parsed");
        }

        protected void onSuccess() {
            log.add("success");
        }

        protected void onFailure(Exception e) {
            log.add("failure");
        }

        protected void onResponse(Message message) {
            log.add("response");
        }
    }
}