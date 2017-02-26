package org.zells.dish.network;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.connecting.Connection;

public interface SignalListener {

    void onDeliver(Delivery delivery);

    void onJoin(Connection connection);

    void onLeave(Connection connection);
}
