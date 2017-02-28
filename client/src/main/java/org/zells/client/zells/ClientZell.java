package org.zells.client.zells;

import org.zells.client.Client;
import org.zells.client.CommandLineInterface;
import org.zells.client.User;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.connecting.ConnectionRepository;

public class ClientZell implements Zell {

    private User user;
    private Client client;
    private Dish dish;
    private ConnectionRepository connections;
    private CommandLineInterface cli;

    public ClientZell(Client client, User user, Dish dish, ConnectionRepository connections,
                      CommandLineInterface cli) {
        this.user = user;
        this.client = client;
        this.dish = dish;
        this.connections = connections;
        this.cli = cli;
    }

    @Override
    public void receive(Message message) {
        if (message.read(0).asString().equals("exit")) {
            user.tell("Good-bye");
            client.exit();
        } else if (message.read(0).asString().equals("join")) {
            String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
            String port = message.read("port").isNull() ? "42420" : message.read("port").asString();
            String description = "tcp:" + host + ":" + port;
            dish.join(connections.getConnectionOf(description));
            user.tell("Joined " + description);
        } else if (message.read(0).asString().equals("leave")) {
            String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
            String port = message.read("port").isNull() ? "42420" : message.read("port").asString();
            String description = "tcp:" + host + ":" + port;
            dish.leave(connections.getConnectionOf(description));
            user.tell("Left " + description);
        } else if (message.read(0).asString().equals("listen")) {
            Address address = dish.add(new ListenerZell(cli));
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
                user.tell(output.toString().trim());
            }
        } else {
            user.tell("Did not understand: " + message);
        }
    }
}
