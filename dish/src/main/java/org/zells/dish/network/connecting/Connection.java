package org.zells.dish.network.connecting;

import java.io.IOException;

public interface Connection {

    Packet transmit(Packet packet) throws IOException;

    void setHandler(PacketHandler handler);

    Connection open();

    void close();
}
