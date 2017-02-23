package org.zells.dish.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Delivery;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;
import org.zells.dish.network.Signal;
import org.zells.dish.network.encoding.Encoding;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.DeliverSignal;
import org.zells.dish.network.signals.FailedSignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;
import org.zells.dish.util.Uuid;

import java.util.List;

public class EncodeSignalsTest {

    private List<Encoding> encodings;

    @Before
    public void SetUp() {
        encodings = EncodingRepository.supportedEncodings();
    }

    @Test
    public void encodingFailsForUnknownSignal() {
        for (Encoding encoding : encodings) {
            Exception caught = tryToEncode(encoding);
            assert caught != null;
            assert caught.getMessage().startsWith("unsupported signal type");
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
        assertEncodeDecode(deliverSignal(new NullMessage()));
        assertEncodeDecode(deliverSignal(new StringMessage("message")));
        assertEncodeDecode(deliverSignal(new BooleanMessage(true)));
        assertEncodeDecode(deliverSignal(new IntegerMessage(42)));
        assertEncodeDecode(deliverSignal(new BinaryMessage(new byte[]{1, 42, 27})));
        assertEncodeDecode(deliverSignal(new CompositeMessage()
                .put("one", new StringMessage("uno"))
                .put("and", new CompositeMessage()
                        .put("two", new IntegerMessage(2)))));
    }

    private DeliverSignal deliverSignal(Message message) {
        return new DeliverSignal(new Delivery(Uuid.fromString("01"), Address.fromString("aa"), message));
    }

    @Test
    public void join() {
        assertEncodeDecode(new JoinSignal("foo:connection"));
    }

    private Exception tryToEncode(Encoding encoding) {
        try {
            encoding.encode(new Signal() {
            });
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    private void assertEncodeDecode(Signal signal) {
        for (Encoding encoding : encodings) {
            assert encoding.decode(encoding.encode(signal)).equals(signal);
        }
    }
}
