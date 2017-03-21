package org.zells.client;

import org.zells.client.zells.AddressBookZell;
import org.zells.client.zells.CortexZell;
import org.zells.dish.Dish;
import org.zells.dish.network.connecting.ConnectionRepository;

import java.io.IOException;

public class Cortex {

    public Dish dish;
    AddressBookZell book;

    public static void main(String[] args) throws IOException {
        CortexGui.start(new Cortex(
                Dish.buildDefault(),
                new ConnectionRepository().addAll(ConnectionRepository.supportedConnections())));
    }

    private Cortex(Dish dish, ConnectionRepository connections) {
        this.dish = dish;
        this.book = new AddressBookZell(dish);

        book.put("book", dish.add(book));
        book.put("cortex", dish.add(new CortexZell(this, dish, connections)));
    }

    void stop() {
        dish.leaveAll();
    }
}
