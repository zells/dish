package org.zells.dish.tests.fakes;

import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;

class FakePacket extends Packet {

    private Signal signal;

    FakePacket(Signal signal) {
        super(new byte[0]);
        this.signal = signal;
    }

    Signal getSignal() {
        return signal;
    }
}
