package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommandLineInterface {

    private final Dish dish;
    private final User user;
    private Map<String, Address> aliases = new HashMap<String, Address>();
    private List<Message> received = new ArrayList<Message>();

    CommandLineInterface(User user, Dish dish) {
        this.user = user;
        this.dish = dish;

        user.listen(new InputListener());
    }

    void receive(Message message) {
        user.tell(received.size() + "> " + message.toString());
        received.add(message);
    }

    Map<String, Address> getAliases() {
        return aliases;
    }

    void setAlias(String alias, Address address) {
        aliases.put(alias, address);
    }

    void removeAlias(String alias) {
        if (!aliases.containsKey(alias)) {
            throw new RuntimeException("No such alias: " + alias);
        }
        aliases.remove(alias);
    }

    private void send(String receiver, Message message) {
        try {
            dish.send(resolveAddress(receiver), message);
        } catch (Exception e) {
            user.tell("Error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
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
            } catch (Exception e) {
                user.tell("Parsing error: " + e.getMessage());
                return;
            }

            send(parser.getReceiver(), parser.getMessage());
        }

    }
}
