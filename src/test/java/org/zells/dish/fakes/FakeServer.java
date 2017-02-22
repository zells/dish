package org.zells.dish.fakes;

import org.zells.dish.network.*;
import org.zells.dish.network.Packet;

public class FakeServer implements Server {

    private SignalListener listener;
    private int id;

    public FakeServer(int id) {
        this.id = id;
    }

    public void start(SignalListener listener) {
        this.listener = listener;
    }

    public void stop() {
    }

    public Connection getConnection() {
        return new FakeConnection(this, this.id);
    }

    Packet receive(Packet packet) {
        return new FakePacket(listener.respondTo(((FakePacket)packet).getSignal()));
    }
}
