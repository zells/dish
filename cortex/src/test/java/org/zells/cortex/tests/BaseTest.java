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
    protected AddressBookZell book;

    private Communicator communicator;

    @Before
    public void SetUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository()) {
            @Override
            public Messenger send(Address receiver, Message message) {
                return super.send(receiver, message).sync();
            }
        };
        target = dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
        book = new AddressBookZell(dish);
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

    protected void fail(String input, String errorMessage) {
        try {
            send(input);
            assert false;
        } catch (Exception e) {
            assert e.getMessage().contains(errorMessage);
        }
    }

    public class Listener extends Communicator.Listener {

        protected void onParsed(String receiver, Message message) {
            log.add("parsed");
        }

        protected void onSuccess() {
            log.add("success");
        }

        protected void onFailure(Exception e) {
            log.add("failure");
        }

        protected void onResponse(int sequence, Message message) {
            log.add("response");
        }
    }
}
