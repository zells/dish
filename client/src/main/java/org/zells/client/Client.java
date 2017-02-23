package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

public class Client {

    private final User user;

    public static void main(String[] args) {
        new Client("localhost", 42420);
    }

    private Client(String host, int port) {
        this(new ConsoleUser(), Dish.buildDefault(host, port));
    }

    public Client(User user, Dish dish) {
        this.user = user;

        Address me = dish.add(new ClientZell());
        user.tell("Hi. I am " + me);

        new CommandLineInterface(user, dish);
    }

    private class ClientZell implements Zell {
        public void receive(Message message) {
            user.tell("Received " + message);
        }
    }
}
