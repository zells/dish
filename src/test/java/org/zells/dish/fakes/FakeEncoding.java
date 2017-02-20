package org.zells.dish.fakes;

import org.zells.dish.network.encoding.Encoding;
import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;
import org.zells.dish.network.signals.DeliverySignal;
import org.zells.dish.network.signals.JoinSignal;

public class FakeEncoding implements Encoding {

    public Packet encode(DeliverySignal signal) {
        return new FakePacket(signal);
    }

    public Packet encode(JoinSignal signal) {
        return new FakePacket(signal);
    }

    public Signal decode(Packet packet) {
        return ((FakePacket)packet).getSignal();
    }
}
