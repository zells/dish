package org.zells.cortex.zells;

import org.zells.cortex.Cortex;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;

import java.util.HashMap;
import java.util.Map;

public class CortexZell implements Zell {

    private Cortex cortex;
    private Dish dish;
    private ConnectionRepository connections;
    private Map<Integer, Server> servers = new HashMap<Integer, Server>();

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
            servers.put(port, connections.buildServer(port).start(dish));
        } else if (!message.read("stop").isNull()) {
            int port = message.read("stop").asInteger();
            servers.get(port).stop();
        }
    }
}
