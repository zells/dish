package org.zells.dish.tests.fakes;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.PacketHandler;

public class FakeConnection implements Connection {

    private PacketHandler handler;
    private FakeConnection other;

    public Packet transmit(Packet packet) {
        return other.handler.handle(packet);
    }

    public void setHandler(PacketHandler handler) {
        this.handler = handler;
    }

    public Connection open() {
        return this;
    }

    public void close() {
    }

    public void to(FakeConnection other) {
        this.other = other;
    }
}
