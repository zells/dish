package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketConnection;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private final Dish dish;
    private Address lobby;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        parseArguments(args, main);
        System.out.println("Lobby is at " + main.getLobby());
    }

    private static void parseArguments(String[] args, Main app) throws IOException {
        ConnectionRepository connections = new ConnectionRepository()
                .addAll(ConnectionRepository.supportedConnections());

        for (String arg : args) {
            if (arg.startsWith("-s")) {
                int port = Integer.parseInt(arg.substring(2));

                app.startServer(port);
                System.out.println("Started server on port " + port);
            } else if (arg.startsWith("-j")) {
                String description = arg.substring(2);

                app.join(connections.getConnectionOf(description));
                System.out.println("Joined " + description);
            } else if (arg.startsWith("-d")) {
                TcpSocketConnection.loggingEnabled = true;
                System.out.println("Debug mode");
            }
        }
    }

    private Main() {
        dish = Dish.buildDefault();
        lobby = dish.add(new LobbyZell(dish));
    }

    private void join(Connection connection) {
        dish.join(connection);
    }

    private void startServer(int port) throws IOException {
        new TcpSocketServer(new ServerSocket(port)).start(dish);
    }

    private Address getLobby() {
        return lobby;
    }
}
