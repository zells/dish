package org.zells.dish.fakes;

import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;

public class FakePacket implements Packet {

    private Signal signal;

    public FakePacket(Signal signal) {
        this.signal = signal;
    }

    public Signal getSignal() {
        return signal;
    }
}
