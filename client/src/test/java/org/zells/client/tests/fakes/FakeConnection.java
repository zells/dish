package org.zells.client.tests.fakes;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.PacketHandler;

import java.io.IOException;

public class FakeConnection implements Connection {

    private String description;

    public FakeConnection(String description) {
        this.description = description;
    }

    @Override
    public Packet transmit(Packet packet) throws IOException {
        return null;
    }

    @Override
    public void setHandler(PacketHandler handler) {
    }

    @Override
    public Connection open() {
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FakeConnection
                && description.equals(((FakeConnection) obj).description);
    }
}
