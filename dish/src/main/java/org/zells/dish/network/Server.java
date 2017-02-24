package org.zells.dish.network;

import org.zells.dish.Dish;

public interface Server {

    Server start(Dish dish);

    void stop();
}
