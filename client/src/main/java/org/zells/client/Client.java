package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.util.Arrays;

public class Client {

    private final User user;
    private final Dish dish;

    public static void main(String[] args) {
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 42420;
        System.out.println(port);
        new Client("localhost", port);
    }

    private Client(String host, int port) {
        this(new ConsoleUser(), Dish.buildDefault(host, port));
    }

    public Client(User user, Dish dish) {
        this.user = user;
        this.dish = dish;

        Address me = dish.add(new ClientZell());
        user.tell("Hi. I am " + me);

        new CommandLineInterface(user, dish);
    }

    private class ClientZell implements Zell {
        public void receive(Message message) {
            if (message.read(0).asString().equals("connect")) {
                dish.connect("tcp:localhost:" + message.read("port").asString());
            } else if (message.read(0).asString().equals("join")) {
                dish.join("tcp:localhost:" + message.read("port").asString());
            } else {
                user.tell("Did not understand: " + message);
            }
        }
    }
}
