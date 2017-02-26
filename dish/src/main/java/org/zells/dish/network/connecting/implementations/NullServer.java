package org.zells.dish.network.connecting.implementations;

import org.zells.dish.Dish;
import org.zells.dish.network.connecting.Server;

public class NullServer implements Server {

    public Server start(Dish dish) {
        return this;
    }

    public void stop() {
    }
}
