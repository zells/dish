package org.zells.dish.network.connections;

import org.zells.dish.Dish;
import org.zells.dish.network.Server;

public class NullServer implements Server {

    public Server start(Dish dish) {
        return this;
    }

    public void stop() {
    }
}
