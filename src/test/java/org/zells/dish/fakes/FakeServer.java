package org.zells.dish.fakes;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Server;
import org.zells.dish.network.SignalListener;

public class FakeServer implements Server {

    private SignalListener listener;

    public void start(SignalListener listener) {
        this.listener = listener;
    }

    public void stop() {
    }

    public Connection getConnection() {
        return new FakeConnection(this);
    }

    public Packet receive(Packet packet) {
        return new FakePacket(listener.respond(((FakePacket)packet).getSignal()));
    }
}
