package org.zells.dish.fakes;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Server;

public class FakeConnection implements Connection {

    private FakeServer server;
    private final int id;

    FakeConnection(Server server, int id) {
        this.server = (FakeServer)server;
        this.id = id;
    }

    public Packet transmit(Packet signal) {
        return server.receive(signal);
    }

    public String getDescription() {
        return "fake:" + id;
    }
}
