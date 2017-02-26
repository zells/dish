package org.zells.dish.network.connecting.implementations;

import org.zells.dish.Dish;
import org.zells.dish.network.connecting.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class TcpSocketServer implements Server {

    private ServerSocket server;

    private boolean running = false;
    private List<TcpSocketConnection> connections = new ArrayList<TcpSocketConnection>();

    public TcpSocketServer(ServerSocket server) {
        this.server = server;
    }

    public TcpSocketServer start(final Dish dish) {
        running = true;
        new Thread(new Runnable() {
            public void run() {
                while (running) {
                    try {
                        TcpSocketConnection connection = new TcpSocketConnection(server.accept()).open();
                        connections.add(connection);
                        dish.listen(connection);
                    } catch (IOException ignored) {
                    }
                }
            }
        }).start();

        return this;
    }

    public void stop() {
        running = false;
        try {
            server.close();
            for (TcpSocketConnection connection : connections) {
                connection.close();
            }
        } catch (IOException ignored) {
        }
    }
}