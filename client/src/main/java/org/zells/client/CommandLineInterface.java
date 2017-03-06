package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandLineInterface {

    private final Dish dish;
    private final User user;
    private Map<String, Address> aliases = new HashMap<String, Address>();
    private List<Message> received = new ArrayList<Message>();

    CommandLineInterface(User user, Dish dish) {
        this.user = user;
        this.dish = dish;

        user.listen(new InputListener());
    }

    public void receive(Message message) {
        user.tell(received.size() + "> " + message.toString());
        received.add(message);
    }

    public Map<String, Address> getAliases() {
        return aliases;
    }

    public void setAlias(String alias, Address address) {
        aliases.put(alias, address);
    }

    public void removeAlias(String alias) {
        if (!aliases.containsKey(alias)) {
            throw new RuntimeException("No such alias: " + alias);
        }
        aliases.remove(alias);
    }

    private void send(Address receiver, Message message) {
        final boolean[] done = new boolean[1];

        dish.send(receiver, message)
            .when(new Messenger.Delivered() {
                @Override
                public void then() {
                    done[0] = true;
                }
            })
            .when(new Messenger.Failed() {
                @Override
                public void then(Exception e) {
                    user.tell("Error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
                    done[0] = true;
                }
            });

        while (!done[0]) {
            Thread.yield();
        }
    }

    private Address resolveAddress(String receiver) {
        if (receiver.startsWith("0x")) {
            return Address.fromString(receiver);
        } else if (aliases.containsKey(receiver)) {
            return aliases.get(receiver);
        } else {
            return Address.fromString(receiver);
        }
    }

    private class InputListener implements User.InputListener {
        public void hears(String input) {
            if (input.length() == 0) {
                return;
            }

            InputParser parser;
            try {
                parser = new InputParser(input, received, aliases);
                Address receiver = resolveAddress(parser.getReceiver());
                send(receiver, parser.getMessage());
            } catch (Exception e) {
                user.tell("Parsing error: " + e.getMessage());
            }
        }

    }
}
