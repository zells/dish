package org.zells.dish.tests.fakes;

import org.zells.dish.network.Signal;
import org.zells.dish.network.connecting.Packet;

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
