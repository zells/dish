package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyZell implements Zell {

    private Dish dish;
    private Map<Address, String> people = new HashMap<Address, String>();
    private Map<String, List<Address>> subscribers = new HashMap<String, List<Address>>();

    public LobbyZell(Dish dish) {
        this.dish = dish;
    }

    public void receive(Message message) {
        if (!message.read("enter").isNull() && !message.read("as").isNull()) {
            Address address = message.read("enter").asAddress();
            String name = message.read("as").asString();

            if (people.containsKey(address)) {
                dish.send(address, new StringMessage("You are already there as [" + people.get(address) + "]"));
            } else if (people.containsValue(name)) {
                dish.send(address, new StringMessage("There is already somebody here with that name."));
            } else {
                people.put(address, name);
            }

        } else if (!message.read("hello").isNull()) {
            dish.send(message.read("hello").asAddress(), new StringMessage("Currently here: " + people.values()));

        } else if (!message.read("leave").isNull()) {
            Address address = message.read("leave").asAddress();

            people.remove(address);
            dish.send(address, new StringMessage("Good-bye"));

        } else if (!message.read("inform").isNull() && !message.read("about").isNull()) {
            Address address = message.read("inform").asAddress();
            String topic = message.read("about").asString();

            if (!subscribers.containsKey(topic)) {
                subscribers.put(topic, new ArrayList<Address>());
            }
            subscribers.get(topic).add(address);

        } else if (!message.read("spare").isNull() && !message.read("about").isNull()) {
            Address address = message.read("spare").asAddress();
            String topic = message.read("about").asString();

            if (subscribers.containsKey(topic)) {
                subscribers.get(topic).remove(address);
            }

        } else if (!message.read("say").isNull() && !message.read("as").isNull()) {
            Address sender = message.read("as").asAddress();
            Message said = message.read("say");

            Message out = new CompositeMessage()
                    .put("message", said)
                    .put("from", new StringMessage(people.get(sender)));

            Message to = message.read("to");
            Message topic = message.read("regarding");
            for (Address person : people.keySet()) {
                if (person.equals(sender)) {
                    continue;
                } else if (!to.isNull() && !to.asString().equals(people.get(person))) {
                    continue;
                } else if (!topic.isNull()) {
                    continue;
                }

                dish.send(person, out);
            }

            if (!topic.isNull() && subscribers.containsKey(topic.asString())) {
                Message topicOut = new CompositeMessage()
                        .put("message", said)
                        .put("regarding", topic)
                        .put("from", new StringMessage(people.get(sender)));

                for (Address subscriber : subscribers.get(topic.asString())) {
                    dish.send(subscriber, topicOut);
                }
            }
        }
    }
}
