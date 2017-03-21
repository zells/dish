package org.zells.client.zells;

import org.zells.client.Cortex;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class CortexZell implements Zell {

    private Cortex cortex;
    private Dish dish;
    private ConnectionRepository connections;

    public CortexZell(Cortex cortex, Dish dish, ConnectionRepository connections) {
        this.cortex = cortex;
        this.dish = dish;
        this.connections = connections;
    }

    @Override
    public void receive(Message message) {
        if (!message.read("say").isNull() && !message.read("to").isNull()) {
            Message say = message.read("say");
            Address to = message.read("to").asAddress();
            dish.send(to, say);
        } else if (message.read(0).asString().equals("join")) {
            String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
            String port = message.read("port").isNull() ? "42420" : message.read("port").asString();
            String description = "tcp:" + host + ":" + port;

            dish.join(connections.getConnectionOf(description));
            System.out.println("Joined " + description);
        } else if (message.read(0).asString().equals("leave")) {
            String host = message.read("host").isNull() ? "localhost" : message.read("host").asString();
            String port = message.read("port").isNull() ? "42420" : message.read("port").asString();
            String description = "tcp:" + host + ":" + port;

            dish.leave(connections.getConnectionOf(description));
            System.out.println("Left " + description);
        } else if (!message.read("listen").isNull()) {
            int port = message.read("listen").asInteger();

            try {
                new TcpSocketServer(new ServerSocket(port)).start(dish);
                System.out.println("Started server on port " + port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
