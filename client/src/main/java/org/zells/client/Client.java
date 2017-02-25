package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.Server;
import org.zells.dish.network.connections.NullServer;
import org.zells.dish.network.connections.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Client {

    private final User user;
    private final Dish dish;
    private final Server server;
    private final ConnectionRepository connections;

    private final CommandLineInterface cli;

    public static void main(String[] args) throws IOException {
        Server server = new NullServer();
        if (args.length == 1) {
            int port = Integer.parseInt(args[0]);
            server = new TcpSocketServer(new ServerSocket(port));
            System.out.println("Started server on port " + port);
        }

        new Client(
                Dish.buildDefault(),
                server,
                new ConsoleUser(),
                new ConnectionRepository().addAll(ConnectionRepository.supportedConnections()));
    }

    public Client(Dish dish, Server server, User user, ConnectionRepository connections) {
        this.user = user;
        this.dish = dish;
        this.server = server.start(dish);
        this.connections = connections;

        cli = new CommandLineInterface(user, dish);
        cli.setAlias("client", dish.add(new ClientZell()));

        server.start(dish);
    }

    private class ClientZell implements Zell {
        @Override
        public void receive(Message message) {
            if (message.read(0).asString().equals("exit")) {
                user.tell("Good-bye");

                if (server != null) {
                    server.stop();
                }
                dish.leaveAll();
                user.stop();
            } else if (message.read(0).asString().equals("join")) {
                String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
                String description = "tcp:" + host + ":" + message.read("port").asString();
                dish.join(connections.getConnectionOf(description));
                user.tell("Joined " + description);
            } else if (message.read(0).asString().equals("leave")) {
                String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
                String description = "tcp:" + host + ":" + message.read("port").asString();
                dish.leave(connections.getConnectionOf(description));
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
                    user.tell(output.toString().trim());
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
