package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LobbyZell implements Zell {

    private Dish dish;
    private Map<String, AvatarZell> avatars = new HashMap<String, AvatarZell>();
    private Map<String, Set<String>> topics = new HashMap<String, Set<String>>();

    public LobbyZell(Dish dish) {
        this.dish = dish;
    }

    public void receive(Message message) {
        if (message.read(0).asString().equals("enter") && !message.read("from").isNull() && !message.read("as").isNull()) {
            Address address = message.read("from").asAddress();
            final String name = message.read("as").asString();

            try {
                Address avatarAddress = enter(name);
                dish.send(address, new CompositeMessage()
                        .put(0, new StringMessage("Hello, " + name))
                        .put("avatar", new AddressMessage(avatarAddress)));
            } catch (Exception e) {
                dish.send(address, new CompositeMessage()
                        .put("error", new StringMessage(e.getMessage())));
            }


        } else if (message.read(0).asString().equals("hello") && !message.read("from").isNull()) {
            Address address = message.read("from").asAddress();

            ArrayList<String> names = new ArrayList<String>(avatars.keySet());
            Collections.sort(names);
            CompositeMessage people = new CompositeMessage();
            for (int i = 0; i < names.size(); i++) {
                people.put(i, new StringMessage(names.get(i)));
            }

            dish.send(address, new CompositeMessage()
                    .put("people", people));
        }
    }

    private Address enter(String name) {
        if (avatars.containsKey(name)) {
            throw new RuntimeException("There is already somebody here with that name.");
        }

        AvatarZell avatar = new AvatarZell(name);
        avatars.put(name, avatar);

        Address address = dish.add(avatar);
        avatar.address = address;

        return address;
    }

    private void leave(String name) {
        dish.remove(avatars.get(name).address);
        avatars.remove(name);
        for (String topic : topics.keySet()) {
            topics.get(topic).remove(name);
        }
    }

    private void sayToAll(Message message, String from) {
        for (String name : avatars.keySet()) {
            if (!name.equals(from)) {
                sayToOne(message, from, name);
            }
        }
    }

    private void sayToOne(Message message, String from, String to) {
        avatars.get(to).hear(message, from);
    }

    private void sayOnTopic(Message message, String from, String topic) {
        if (!topics.containsKey(topic)) {
            return;
        }

        for (String name : topics.get(topic)) {
            if (!name.equals(from)) {
                avatars.get(name).hearOnTopic(message, from, topic);
            }
        }
    }

    private void joinTopic(String name, String topic) {
        if (!topics.containsKey(topic)) {
            topics.put(topic, new HashSet<String>());
        }
        topics.get(topic).add(name);
    }

    private void ignoreTopic(String name, String topic) {
        if (topics.containsKey(topic)) {
            topics.get(topic).remove(name);
        }
    }

    private class AvatarZell implements Zell {

        private final String name;
        private Set<Address> subscribers = new HashSet<Address>();
        private LinkedList<Message> heard = new LinkedList<Message>();
        private int sequence = 1;
        private Address address;

        AvatarZell(String name) {
            this.name = name;
        }

        private void hear(Message message, String from) {
            notifySubscribers(new CompositeMessage()
                    .put("message", message)
                    .put("from", new StringMessage(from)));
        }

        private void hearOnTopic(Message message, String from, String topic) {
            notifySubscribers(new CompositeMessage()
                    .put("message", message)
                    .put("from", new StringMessage(from))
                    .put("on", new StringMessage(topic)));
        }

        @Override
        public void receive(Message message) {
            if (message.read("subscribe") instanceof AddressMessage) {
                Address subscriber = message.read("subscribe").asAddress();
                subscribers.add(subscriber);
                for (Message m : heard) {
                    dish.send(subscriber, m);
                }
            } else if (message.read("say") instanceof StringMessage) {
                Message said = message.read("say");
                if (message.read("to") instanceof StringMessage) {
                    sayToOne(said, name, message.read("to").asString());
                } else if (message.read("on") instanceof StringMessage) {
                    sayOnTopic(said, name, message.read("on").asString());
                } else {
                    sayToAll(said, name);
                }
            } else if (message.read("join") instanceof StringMessage) {
                joinTopic(name, message.read("join").asString());
            } else if (message.read("ignore") instanceof StringMessage) {
                ignoreTopic(name, message.read("ignore").asString());
            } else if (message.read(0).equals(new StringMessage("leave"))) {
                leave(name);
                notifySubscribers(new StringMessage("Good-bye"));
            }
        }

        private void notifySubscribers(Message about) {
            CompositeMessage message = new CompositeMessage()
                    .put("message", about)
                    .put("time", new StringMessage(getTimeAsIsoString()))
                    .put("sequence", new IntegerMessage(sequence++));
            heard.add(message);

            for (final Address subscriber : subscribers) {
                dish.send(subscriber, message)
                        .when(new Messenger.Failed() {
                            @Override
                            public void then(Exception e) {
                                subscribers.remove(subscriber);
                            }
                        });
            }
        }

        private String getTimeAsIsoString() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format(new Date());
        }

    }
}
