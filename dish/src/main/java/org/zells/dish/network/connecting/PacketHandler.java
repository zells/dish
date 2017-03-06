package org.zells.dish.network.connecting;

import org.zells.dish.network.Signal;
import org.zells.dish.network.SignalListener;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.*;

public class PacketHandler {

    private EncodingRepository encodings;
    private SignalListener listener;
    private Connection connection;

    public PacketHandler(EncodingRepository encodings, Connection connection, SignalListener listener) {
        this.encodings = encodings;
        this.listener = listener;
        this.connection = connection;
    }

    public Packet handle(Packet packet) {
        Signal signal = encodings.decode(packet);
        Signal response = respond(signal);
        return encodings.encode(response);
    }

    private Signal respond(Signal signal) {
        try {
            if (onSignal(signal)) {
                return new OkSignal();
            } else {
                return new FailedSignal();
            }
        } catch (Exception e) {
            return new FailedSignal(e.getMessage());
        }
    }

    private boolean onSignal(Signal signal) {
        if (signal instanceof JoinSignal) {
            return listener.onJoin(connection);
        } else if (signal instanceof LeaveSignal) {
            return listener.onLeave(connection);
        } else if (signal instanceof DeliverSignal) {
            return listener.onDeliver(((DeliverSignal) signal).getDelivery());
        }

        throw new RuntimeException("Unexpected signal: " + signal.getClass());
    }
}