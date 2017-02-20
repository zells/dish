package org.zells.dish.fakes;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Server;

public class FakeConnection implements Connection {

    private FakeServer server;

    public FakeConnection(Server server) {
        this.server = (FakeServer)server;
    }

    public Packet transmit(Packet packet) {
        return server.receive(packet);
    }
}
