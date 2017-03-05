package org.zells.client;

import org.zells.client.zells.ClientZell;
import org.zells.dish.Dish;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketConnection;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Client {

    private Dish dish;
    private Server server;
    private User user;

    public static void main(String[] args) throws IOException {
        Client client = new Client(
                Dish.buildDefault(),
                new ConsoleUser(),
                new ConnectionRepository().addAll(ConnectionRepository.supportedConnections()));
        parseArguments(args, client);
    }

    private static void parseArguments(String[] args, Client client) throws IOException {
        for (String arg : args) {
            if (arg.startsWith("-s")) {
                int port = Integer.parseInt(arg.substring(2));
                client.startServer(port);
                System.out.println("Started server on port " + port);
            } else if (arg.startsWith("-d")) {
                TcpSocketConnection.loggingEnabled = true;
                System.out.println("Debug mode");
            }
        }
    }

    private void startServer(int port) throws IOException {
        server = new TcpSocketServer(new ServerSocket(port)).start(dish);
    }

    public Client(Dish dish, User user, ConnectionRepository connections) {
        this.dish = dish;
        this.user = user;

        CommandLineInterface cli = new CommandLineInterface(user, dish);
        cli.setAlias("client", dish.add(new ClientZell(this, user, dish, connections, cli)));
    }

    public void exit() {
        if (server != null) {
            server.stop();
        }
        dish.leaveAll();
        user.stop();
    }
}
