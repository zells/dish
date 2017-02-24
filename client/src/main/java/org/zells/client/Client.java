package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

public class Client {

    private final User user;
    private final Dish dish;
    private final CommandLineInterface cli;

    public static void main(String[] args) {
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 42420;
        new Client("localhost", port);
    }

    private Client(String host, int port) {
        this(Dish.buildDefault(host, port), new ConsoleUser());
    }

    public Client(Dish dish, User user) {
        this.user = user;
        this.dish = dish;

        cli = new CommandLineInterface(user, dish);
        cli.setAlias("client", dish.add(new ClientZell()));
    }

    private class ClientZell implements Zell {
        @Override
        public void receive(Message message) {
            if (message.read(0).asString().equals("exit")) {
                user.tell("Good-bye");
                dish.stop();
                user.stop();
            } else if (message.read(0).asString().equals("join")) {
                String description = "tcp:localhost:" + message.read("port").asString();
                dish.join(description);
                user.tell("Joined " + description);
            } else if (message.read(0).asString().equals("leave")) {
                String description = "tcp:localhost:" + message.read("port").asString();
                dish.leave(description);
                user.tell("Left " + description);
            } else if (message.read(0).asString().equals("listen")) {
                Address address = dish.add(new ListenerZell());
                user.tell("Listening on " + address);

                if (message.keys().contains("as")) {
                    String alias = message.read("as").asString();
                    cli.setAlias(alias, address);
                    user.tell("Set alias [" + alias + "] for [" + address + "]");
                }
            } else if (message.read(0).asString().equals("alias")) {
                if (message.keys().contains("use")) {
                    String alias = message.read("use").asString().replace(" ", "");
                    Address address = Address.fromString(message.read("for").asString());

                    cli.setAlias(alias, address);
                    user.tell("Set alias [" + alias + "] for [" + address + "]");
                } else if (message.keys().contains("remove")) {
                    String alias = message.read("remove").asString();
                    cli.removeAlias(alias);
                    user.tell("Removed alias [" + alias + "]");
                } else {
                    StringBuilder output = new StringBuilder();
                    output.append("Aliases:\n");
                    for (String alias : cli.getAliases().keySet()) {
                        output.append(alias)
                                .append(": ")
                                .append(cli.getAliases().get(alias))
                                .append('\n');
                    }
                    user.tell(output.toString());
                }
            } else {
                user.tell("Did not understand: " + message);
            }
        }
    }

    private class ListenerZell implements Zell {
        @Override
        public void receive(Message message) {
            cli.receive(message);
        }
    }
}
