package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.BinaryMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.*;

public class LobbyZell implements Zell {

    private Dish dish;
    private Map<String, AvatarZell> avatars = new HashMap<String, AvatarZell>();
    private Map<String, Set<AvatarZell>> subscribers = new HashMap<String, Set<AvatarZell>>();

    public LobbyZell(Dish dish) {
        this.dish = dish;
    }

    public void receive(Message message) {
        if (!message.read("enter").isNull() && !message.read("as").isNull()) {
            Address address = message.read("enter").asAddress();
            final String name = message.read("as").asString();

            if (avatars.containsKey(name)) {
                dish.send(address, new CompositeMessage()
                        .put("error", new StringMessage("There is already somebody here with that name.")));
                return;
            }

            AvatarZell avatar = new AvatarZell(name, address);
            avatars.put(name, avatar);

            Address avatarAddress = dish.add(avatar);
            dish.send(address, new CompositeMessage()
                    .put(0, new StringMessage("Hello, " + name))
                    .put("avatar", new BinaryMessage(avatarAddress.toBytes())));

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

    private class AvatarZell implements Zell {

        private final String name;
        private Address address;
        private boolean connected = true;
        private LinkedList<Message> mailbox = new LinkedList<Message>();

        AvatarZell(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        @Override
        public void receive(Message message) {
            if (message.read(0).asString().equals("leave")) {
                avatars.remove(name);
                for (Set<AvatarZell> subscribed : subscribers.values()) {
                    subscribed.remove(this);
                }
                dish.send(address, new StringMessage("Good-bye"));
            } else if (!message.read("join").isNull()) {
                String topic = message.read("join").asString();
                if (!subscribers.containsKey(topic)) {
                    subscribers.put(topic, new HashSet<AvatarZell>());
                }
                subscribers.get(topic).add(this);
            } else if (!message.read("ignore").isNull()) {
                String topic = message.read("ignore").asString();
                if (subscribers.containsKey(topic)) {
                    subscribers.get(topic).remove(this);
                }
            } else if (!message.read("connect").isNull()) {
                connected = true;
                address = message.read("connect").asAddress();
                while (!mailbox.isEmpty()) {
                    send(mailbox.removeFirst());
                }
            } else if (!message.read("say").isNull()) {
                CompositeMessage relay = new CompositeMessage()
                        .put("message", message.read("say"))
                        .put("from", new StringMessage(name));

                if (message.read("on").isNull()) {
                    if (message.read("to").isNull()) {
                        for (AvatarZell avatar : avatars.values()) {
                            if (!avatar.name.equals(name)) {
                                avatar.send(relay);
                            }
                        }
                    } else {
                        String to = message.read("to").asString();
                        avatars.get(to).send(relay);
                    }
                } else {
                    String topic = message.read("on").asString();
                    relay.put("on", new StringMessage(topic));
                    if (subscribers.containsKey(topic)) {
                        for (AvatarZell subscriber : subscribers.get(topic)) {
                            subscriber.send(relay);
                        }
                    }
                }
            }
        }

        private void send(final Message message) {
            if (!connected) {
                mailbox.add(message);
                return;
            }

            dish.send(address, message)
                    .when(new Messenger.Failed() {
                        @Override
                        public void then(Exception e) {
                            connected = false;
                            mailbox.add(message);
                        }
                    });
        }

    }
}
