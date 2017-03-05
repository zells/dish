package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketConnection;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Lobby {

    private final Dish dish;
    private Address address;

    public static void main(String[] args) throws IOException {
        Lobby lobby = new Lobby();
        parseArguments(args, lobby);
        System.out.println("Lobby is at " + lobby.getAddress());
    }

    private static void parseArguments(String[] args, Lobby lobby) throws IOException {
        ConnectionRepository connections = new ConnectionRepository()
                .addAll(ConnectionRepository.supportedConnections());

        for (String arg : args) {
            if (arg.startsWith("-s")) {
                int port = Integer.parseInt(arg.substring(2));

                lobby.startServer(port);
                System.out.println("Started server on port " + port);
            } else if (arg.startsWith("-j")) {
                String description = arg.substring(2);

                lobby.join(connections.getConnectionOf(description));
                System.out.println("Joined " + description);
            } else if (arg.startsWith("-d")) {
                TcpSocketConnection.loggingEnabled = true;
                System.out.println("Debug mode");
            }
        }
    }

    private Lobby() {
        dish = Dish.buildDefault();
        address = dish.add(new LobbyZell(dish));
    }

    private void join(Connection connection) {
        dish.join(connection);
    }

    private void startServer(int port) throws IOException {
        new TcpSocketServer(new ServerSocket(port)).start(dish);
    }

    private Address getAddress() {
        return address;
    }
}
