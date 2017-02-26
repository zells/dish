package org.zells.dish.tests.fakes;

import org.zells.dish.network.connecting.Packet;
import org.zells.dish.network.Signal;
import org.zells.dish.network.encoding.Encoding;

public class FakeEncoding implements Encoding {

    public Packet encode(Signal signal) {
        return new FakePacket(signal);
    }

    public Signal decode(Packet packet) {
        return ((FakePacket)packet).getSignal();
    }
}
