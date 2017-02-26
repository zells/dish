package org.zells.dish.network.encoding;

import org.zells.dish.network.connecting.Packet;
import org.zells.dish.network.Signal;

public interface Encoding {

    Packet encode(Signal signal);

    Signal decode(Packet packet);
}
