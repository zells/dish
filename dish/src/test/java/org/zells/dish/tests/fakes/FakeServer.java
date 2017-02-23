package org.zells.dish.tests.fakes;

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

    public String getConnectionDescription() {
        return "fake:" + id;
    }

    Packet receive(Packet packet) {
        return new FakePacket(listener.respondTo(((FakePacket)packet).getSignal()));
    }
}
