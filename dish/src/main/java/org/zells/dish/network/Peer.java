package org.zells.dish.network;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.*;

import java.io.IOException;

public class Peer {

    private Connection connection;
    private EncodingRepository encodings;

    public Peer(EncodingRepository encodings, Connection connection) {
        this.connection = connection;
        this.encodings = encodings;
    }

    public boolean deliver(Delivery delivery) {
        Signal response = signal(new DeliverSignal(delivery));
        return response instanceof OkSignal;
    }

    public void join() {
        signal(new JoinSignal());
    }

    public void leave() {
        signal(new LeaveSignal());
    }

    private Signal signal(Signal signal) {
        try {
            Packet packet = encodings.encode(signal);
            Packet response = connection.transmit(packet);
            return encodings.decode(response);
        } catch (IOException e) {
            return new FailedSignal(e.getMessage());
        }
    }
}
