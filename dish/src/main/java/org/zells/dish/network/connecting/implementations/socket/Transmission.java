package org.zells.dish.network.connecting.implementations.socket;

import org.zells.dish.network.connecting.Packet;

class Transmission {
    final Packet packet;
    final int id;
    final boolean isResponse;

    Transmission(Packet packet, int id, boolean isResponse) {
        this.packet = packet;
        this.id = id;
        this.isResponse = isResponse;
    }

    Transmission response(Packet packet) {
        return new Transmission(packet, id, true);
    }
}
