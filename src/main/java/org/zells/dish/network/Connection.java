package org.zells.dish.network;

import java.io.IOException;

public interface Connection {

    Packet transmit(Packet packet) throws IOException;
}
