package org.zells.dish;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.DeliverySignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;

class Peer {

    private Connection connection;
    private EncodingRepository encodings;

    Peer(Connection connection, EncodingRepository encodings) {
        this.connection = connection;
        this.encodings = encodings;
    }

    boolean deliver(Delivery delivery) {
        return signal(new DeliverySignal(delivery)) instanceof OkSignal;
    }

    void join(Connection connection) {
        signal(new JoinSignal(connection));
    }

    private Signal signal(Signal signal) {
        Packet packet = encodings.encode(signal);
        Packet response = connection.transmit(packet);
        return encodings.decode(response);
    }
}
