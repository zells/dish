package org.zells.client;

import org.zells.client.zells.AddressBookZell;
import org.zells.client.zells.CortexZell;
import org.zells.dish.Dish;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Cortex {

    public Dish dish;
    public AddressBookZell book;

    public static void main(String[] args) throws IOException {
        CortexGui.start(new Cortex(
                Dish.buildDefault(),
                new ConnectionRepository()
                        .addAll(ConnectionRepository.supportedConnections())
                        .setServerFactory(new ConnectionRepository.ServerFactory() {
                            @Override
                            public Server build(int port) {
                                try {
                                    return new TcpSocketServer(new ServerSocket(port));
                                } catch (IOException e) {
                                    throw new RuntimeException("Failed to build server", e);
                                }
                            }
                        })));
    }

    public Cortex(Dish dish, ConnectionRepository connections) {
        this.dish = dish;
        this.book = new AddressBookZell(dish);

        book.put("book", dish.add(book));
        book.put("cortex", dish.add(new CortexZell(this, dish, connections)));
    }

    void stop() {
        dish.leaveAll();
    }
}
