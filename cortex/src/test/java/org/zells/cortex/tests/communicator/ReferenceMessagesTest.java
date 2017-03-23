package org.zells.cortex.tests.communicator;

import org.junit.Test;
import org.zells.cortex.tests.BaseTest;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.Arrays;
import java.util.Collections;

public class ReferenceMessagesTest extends BaseTest {

    @Override
    public void SetUp() {
        super.SetUp();

        dish.put(Address.fromString("dada"), new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read(0).asAddress(), message.read(1)).sync();
            }
        });
    }

    @Test
    public void notAReference() {
        send(". \"#0\"");
        assert received.equals(Collections.singletonList(new CompositeMessage(new StringMessage("#0"))));
    }

    @Test
    public void invalidReference() {
        fail(". #0", "Invalid reference: 0");
    }

    @Test
    public void countResponses() {
        final String[] received = new String[2];

        send("dada @+ one", new Listener() {
            @Override
            protected void onResponse(int sequence, Message message) {
                received[sequence] = message.asString();
            }
        });
        send("dada @+ two", new Listener() {
            @Override
            protected void onResponse(int sequence, Message message) {
                received[sequence] = message.asString();
            }
        });

        assert Arrays.equals(received, new String[]{"one", "two"});
    }

    @Test
    public void referenceMessage() {
        send("dada @+ one");
        send(". #0");

        assert received.equals(Collections.singletonList(new CompositeMessage(new StringMessage("one"))));
    }

    @Test
    public void mixedContent() {
        send("dada @+ one");
        send(". foo #0 bar");

        assert received.equals(Collections.singletonList(new CompositeMessage(
                new StringMessage("foo"),
                new StringMessage("one"),
                new StringMessage("bar")
        )));
    }

    @Test
    public void partOfMessage() {
        send("dada ![\"@+\", {\"0\":42, \"foo\":\"bar\"}]");
        send(". #0.0 #0.foo");

        assert received.equals(Collections.singletonList(new CompositeMessage(
                new IntegerMessage(42),
                new StringMessage("bar")
        )));
    }

    @Test
    public void deepReference() {
        send("dada ![\"@+\", {\"foo\":{\"bar\":[1, 42]}}]");
        send(". #0.foo.bar.1");

        assert received.equals(Collections.singletonList(new CompositeMessage(
                new IntegerMessage(42)
        )));
    }

    @Test
    public void useReferenceAsReceiver() {
        send("dada @+ .");
        send("#0 foo");

        assert received.equals(Collections.singletonList(new CompositeMessage(
                new StringMessage("foo")
        )));
    }
}