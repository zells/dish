package org.zells.dish.network.encoding;

import org.zells.dish.network.Packet;
import org.zells.dish.network.Signal;
import org.zells.dish.network.encoding.encodings.MsgpackEncoding;

import java.util.ArrayList;
import java.util.List;

public class EncodingRepository {

    private List<Encoding> encodings = new ArrayList<Encoding>();

    public EncodingRepository add(Encoding encoding) {
        encodings.add(encoding);
        return this;
    }

    public EncodingRepository addAll(List<Encoding> encodings) {
        for (Encoding encoding : encodings) {
            add(encoding);
        }
        return this;
    }

    public Packet encode(Signal signal) {
        return encodings.get(0).encode(signal);
    }

    public Signal decode(Packet packet) {
        return encodings.get(0).decode(packet);
    }

    public static List<Encoding> supportedEncodings() {
        ArrayList<Encoding> encodings = new ArrayList<Encoding>();
        encodings.add(new MsgpackEncoding());
        return encodings;
    }
}
