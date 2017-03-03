package org.zells.samples;

import org.zells.dish.Dish;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Lobby {

    public static void main(String[] args) throws IOException {
        Dish dish = Dish.buildDefault();

        ConnectionRepository connections = new ConnectionRepository()
                .addAll(ConnectionRepository.supportedConnections());

        for (String arg : args) {
            if (arg.startsWith("-s")) {
                int port = Integer.parseInt(arg.substring(2));
                new TcpSocketServer(new ServerSocket(port)).start(dish);
                System.out.println("Started server on port " + port);
            } else if (arg.startsWith("-p")) {
                String description = arg.substring(2);
                dish.join(connections.getConnectionOf(description));
                System.out.println("Joined " + description);
            }
        }
    }
}
