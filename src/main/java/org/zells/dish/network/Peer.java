package org.zells.dish.network;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.DeliverSignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;

public class Peer {

    private Connection connection;
    private EncodingRepository encodings;

    public Peer(Connection connection, EncodingRepository encodings) {
        this.connection = connection;
        this.encodings = encodings;
    }

    public boolean deliver(Delivery delivery) {
        return signal(new DeliverSignal(delivery)) instanceof OkSignal;
    }

    public void join(Connection connection) {
        signal(new JoinSignal(connection.getDescription()));
    }

    private Signal signal(Signal signal) {
        Packet packet = encodings.encode(signal);
        Packet response = connection.transmit(packet);
        return encodings.decode(response);
    }
}
