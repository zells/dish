package org.zells.dish.network.connecting;

import org.zells.dish.network.connecting.implementations.socket.TcpSocketConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionRepository {

    private List<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
    private Map<String, Connection> connections = new HashMap<String, Connection>();
    private ServerFactory serverFactory;

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
                try {
                    Connection connection = factory.build(description).open();
                    connections.put(description, connection);
                    return connection;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException("cannot build connection from: " + description);
    }

    public static List<ConnectionFactory> supportedConnections() {
        ArrayList<ConnectionFactory> factories = new ArrayList<ConnectionFactory>();
        factories.add(new ConnectionFactory() {

            public boolean canBuild(String description) {
                return description.startsWith("tcp:");
            }

            public Connection build(String description) throws IOException {
                String[] hostPort = description.substring(4).split(":");
                return new TcpSocketConnection(new Socket(hostPort[0], Integer.parseInt(hostPort[1])));
            }
        });
        return factories;
    }

    public ConnectionRepository setServerFactory(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
        return this;
    }

    public Server buildServer(int port) {
        if (serverFactory == null) {
            throw new RuntimeException("No server factory set");
        }
        return serverFactory.build(port);
    }

    public interface ConnectionFactory {

        boolean canBuild(String description);

        Connection build(String description) throws IOException;
    }

    public interface ServerFactory {

        Server build(int port);
    }
}
