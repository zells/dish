package org.zells.dish;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Delivery;
import org.zells.dish.delivery.messages.NullMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.Signal;
import org.zells.dish.network.encoding.Encoding;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.DeliverSignal;
import org.zells.dish.network.signals.FailedSignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;

import java.util.List;

public class EncodeSignalsTest {

    private List<Encoding> encodings;

    @Before
    public void SetUp() {
        encodings = EncodingRepository.defaultEncodings();
    }

    @Test
    public void encodingFailsForUnknownSignal() {
        Signal signal = new Signal() {
        };

        for (Encoding encoding : encodings) {
            Exception caught = null;
            try {
                encoding.encode(signal);
            } catch (Exception e) {
                caught = e;
            }

            assert caught != null;
            assert caught.getMessage().equals("unsupported signal type: " + signal.getClass());
        }
    }

    @Test
    public void ok() {
        assertEncodeDecode(new OkSignal());
    }

    @Test
    public void failed() {
        assertEncodeDecode(new FailedSignal());
        assertEncodeDecode(new FailedSignal("Because Foo"));
    }

    @Test
    public void deliver() {
        assertEncodeDecode(new DeliverSignal(new Delivery(Address.parse("foo"), new NullMessage(), "uuid")));
        assertEncodeDecode(new DeliverSignal(new Delivery(Address.parse("foo"), new StringMessage("message"), "uuid")));
    }

    @Test
    public void join() {
        assertEncodeDecode(new JoinSignal("foo:connection"));
    }

    private void assertEncodeDecode(Signal signal) {
        for (Encoding encoding : encodings) {
            assert encoding.decode(encoding.encode(signal)).equals(signal);
        }
    }
}
