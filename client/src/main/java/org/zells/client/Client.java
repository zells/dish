package org.zells.client;

import org.zells.client.zells.ClientZell;
import org.zells.dish.Dish;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;
import org.zells.dish.network.connecting.implementations.NullServer;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Client {

    private final Dish dish;
    private final Server server;
    private final User user;

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
        this.dish = dish;
        this.server = server;
        this.user = user;

        CommandLineInterface cli = new CommandLineInterface(user, dish);
        cli.setAlias("client", dish.add(new ClientZell(this, user, dish, connections, cli)));

        server.start(dish);
    }

    public void exit() {
        server.stop();
        dish.leaveAll();
        user.stop();
    }
}
