package org.zells.dish.network.encoding;

import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;
import org.zells.dish.network.signals.DeliverySignal;
import org.zells.dish.network.signals.JoinSignal;

import java.util.ArrayList;
import java.util.List;

public class EncodingRepository {

    private List<Encoding> encodings = new ArrayList<Encoding>();

    public void add(Encoding encoding) {
        encodings.add(encoding);
    }

    public Packet encode(Signal signal) {
        if (signal instanceof DeliverySignal) {
            return encodings.get(0).encode((DeliverySignal) signal);
        } else if (signal instanceof JoinSignal) {
            return encodings.get(0).encode((JoinSignal) signal);
        }

        throw new RuntimeException("Unknown signal");
    }

    public Signal decode(Packet string) {
        return encodings.get(0).decode(string);
    }
}
