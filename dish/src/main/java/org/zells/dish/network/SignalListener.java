package org.zells.dish.network;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.connecting.Connection;

public interface SignalListener {

    boolean onDeliver(Delivery delivery);

    boolean onJoin(Connection connection);

    boolean onLeave(Connection connection);
}
