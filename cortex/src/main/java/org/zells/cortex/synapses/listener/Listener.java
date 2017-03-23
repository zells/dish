package org.zells.cortex.synapses.listener;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Listener {

    private List<ReceivedMessage> receivedMessages = new ArrayList<ReceivedMessage>();

    public Listener(Address target, Dish dish) {
        Address me = dish.add(new ReceiverZell());
        dish.send(target, new CompositeMessage().put("subscribe", new AddressMessage(me)));
    }

    protected void onReceive(ReceivedMessage message) {
    }

    public List<ReceivedMessage> getReceivedMessages() {
        return receivedMessages;
    }

    private void doReceive(Message message) {
        ReceivedMessage receivedMessage = new ReceivedMessage(
                message.read("sequence").asInteger(),
                message.read("message"),
                message.read("time").asString());
        receivedMessages.add(receivedMessage);
        Collections.sort(receivedMessages, new Comparator<ReceivedMessage>() {
            @Override
            public int compare(ReceivedMessage o1, ReceivedMessage o2) {
                return o1.getSequence() - o2.getSequence();
            }
        });
        onReceive(receivedMessage);
    }

    private class ReceiverZell implements Zell {
        @Override
        public void receive(Message message) {
            doReceive(message);
        }

    }

    public static class ReceivedMessage {

        private final int sequence;
        private final Message message;
        private final String time;

        public ReceivedMessage(int sequence, Message message, String time) {
            this.sequence = sequence;
            this.message = message;
            this.time = time;
        }

        public int getSequence() {
            return sequence;
        }

        public Message getMessage() {
            return message;
        }

        public String getTimeIsoString() {
            return time;
        }

        @Override
        public int hashCode() {
            return sequence + message.hashCode() + time.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ReceivedMessage
                    && sequence == ((ReceivedMessage) obj).sequence
                    && message.equals(((ReceivedMessage) obj).message)
                    && time.equals(((ReceivedMessage) obj).time);
        }
    }
}
