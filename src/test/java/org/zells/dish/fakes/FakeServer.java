package org.zells.dish.fakes;

import org.zells.dish.network.*;

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

    Packet receive(Packet packet) {
        return new FakePacket(listener.respondTo(((FakePacket)packet).getSignal()));
    }
}
