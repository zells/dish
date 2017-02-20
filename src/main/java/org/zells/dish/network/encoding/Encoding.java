package org.zells.dish.network.encoding;

import org.zells.dish.network.signals.DeliverySignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;

public interface Encoding {

    Packet encode(DeliverySignal signal);

    Packet encode(JoinSignal signal);

    Signal decode(Packet packet);
}
