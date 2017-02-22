package org.zells.dish.network;

public interface Connection {

    Packet transmit(Packet packet);

    String getDescription();
}
