package org.zells.dish.network;

import org.zells.dish.network.connections.TcpSocketConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionRepository {

    private List<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
    private Map<String, Connection> connections = new HashMap<String, Connection>();

    public void add(ConnectionFactory factory) {
        factories.add(factory);
    }

    public ConnectionRepository addAll(List<ConnectionFactory> factories) {
        for (ConnectionFactory factory : factories) {
            add(factory);
        }
        return this;
    }

    public Connection getConnectionOf(String description) {
        if (connections.containsKey(description)) {
            return connections.get(description);
        }

        for (ConnectionFactory factory : factories) {
            if (factory.canBuild(description)) {
                Connection connection = factory.build(description);
                connections.put(description, connection);
                return connection;
            }
        }

        throw new RuntimeException("cannot build connection from: " + description);
    }

    public static List<ConnectionFactory> supportedConnections() {
        ArrayList<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
        factories.add(new TcpSocketConnection.Factory());
        return factories;
    }
}
