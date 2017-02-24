package org.zells.dish.network;

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
        if (response == null) {
            return new Packet(new byte[0]);
        }
        return encodings.encode(response);
    }

    private Signal respond(Signal signal) {
        try {
            return onSignal(signal);
        } catch (Exception e) {
            return new FailedSignal(e.getMessage());
        }
    }

    private Signal onSignal(Signal signal) {
        if (signal instanceof JoinSignal) {
            listener.onJoin(connection);
            return new OkSignal();
        } else if (signal instanceof LeaveSignal) {
            listener.onLeave(connection);
            return new OkSignal();
        } else if (signal instanceof DeliverSignal) {
            listener.onDeliver(((DeliverSignal) signal).getDelivery());
            return new OkSignal();
        }

        return null;
    }
}
