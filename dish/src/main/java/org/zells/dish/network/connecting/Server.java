package org.zells.dish.network.connecting;

import org.zells.dish.Dish;

public interface Server {

    Server start(Dish dish);

    void stop();
}
