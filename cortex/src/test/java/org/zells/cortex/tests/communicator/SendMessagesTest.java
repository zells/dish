package org.zells.cortex.tests.communicator;

import org.junit.Test;
import org.zells.cortex.tests.BaseTest;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;

import java.util.Arrays;
import java.util.Collections;

public class SendMessagesTest extends BaseTest {

    @Test
    public void invalidMessage() throws Exception {
        fail("!not", "Unrecognized token 'not'");
        assert log.isEmpty();
    }

    @Test
    public void invalidAddress() throws Exception {
        fail("not <", "Invalid hex string: not");
        assert log.isEmpty();
    }

    @Test
    public void nonExistingAddress() throws Exception {
        send("dada <", new Listener() {
            @Override
            protected void onFailure(Exception e) {
                super.onFailure(e);
                assert e.getMessage().equals("Could not find 0xdada");
            }
        });

        assert log.equals(Arrays.asList("parsed", "sending", "failure"));
    }

    @Test
    public void sendToAddress() throws Exception {
        dish.put(Address.fromString("fade"), new Zell() {
            public void receive(Message message) {
                received.add(message);
            }
        });

        send("0xfade <", new Listener() {
            protected void onParsed(String receiver, Message message) {
                super.onParsed(receiver, message);
                assert receiver.equals("0xfade");
                assert message.equals(new NullMessage());
            }
        });

        assert received.equals(Collections.singletonList(new NullMessage()));
        assert log.equals(Arrays.asList("parsed", "sending", "success"));
    }

    @Test
    public void sendToAddressWithoutPrefix() throws Exception {
        dish.put(Address.fromString("fade"), new Zell() {
            public void receive(Message message) {
                received.add(message);
            }
        });

        send("fade <", new Listener() {
            protected void onParsed(String receiver, Message message) {
                super.onParsed(receiver, message);
                assert receiver.equals("fade");
                assert message.equals(new NullMessage());
            }
        });

        assert received.equals(Collections.singletonList(new NullMessage()));
        assert log.equals(Arrays.asList("parsed", "sending", "success"));
    }

    @Test
    public void sendToTarget() throws Exception {
        send(". <", new Listener() {
            protected void onParsed(String receiver, Message message) {
                super.onParsed(receiver, message);
                assert receiver.equals(".");
                assert message.equals(new NullMessage());
            }
        });

        assert received.equals(Collections.singletonList(new NullMessage()));
        assert log.equals(Arrays.asList("parsed", "sending", "success"));
    }

    @Test
    public void implyTarget() {
        send("foo", new Listener() {
            @Override
            protected void onParsed(String receiver, Message message) {
                super.onParsed(receiver, message);
                assert receiver.equals(".");
                assert message.equals(new CompositeMessage(new StringMessage("foo")));
            }
        });

        assert received.equals(Collections.singletonList(new CompositeMessage(new StringMessage("foo"))));
        assert log.equals(Arrays.asList("parsed", "sending", "success"));
    }

    @Test
    public void sendADot() {
        send(".", new Listener() {
            @Override
            protected void onParsed(String receiver, Message message) {
                super.onParsed(receiver, message);
                assert receiver.equals(".");
                assert message.equals(new CompositeMessage(new StringMessage(".")));
            }
        });

        assert received.equals(Collections.singletonList(new CompositeMessage(new StringMessage("."))));
        assert log.equals(Arrays.asList("parsed", "sending", "success"));
    }

    @Test
    public void parseScalarJson() throws Exception {
        send("!null");
        assert received.get(0).equals(new NullMessage());

        send("!\"foo\"");
        assert received.get(1).equals(new StringMessage("foo"));

        send("!42  ");
        assert received.get(2).equals(new IntegerMessage(42));

        send("!true");
        assert received.get(3).equals(new BooleanMessage(true));

        send("!false");
        assert received.get(4).equals(new BooleanMessage(false));

        send("!\"0xbaba\"");
        assert received.get(5).equals(BinaryMessage.fromString("baba"));

        send("!\"@0xbaba\"");
        assert received.get(6).equals(new AddressMessage(Address.fromString("baba")));

        book.put("foo", Address.fromString("dada"));
        send("!\"@foo\"");
        assert received.get(7).equals(new AddressMessage(Address.fromString("dada")));
    }

    @Test
    public void sendExplicitly() throws Exception {
        send(". < !\"hello\"");
        assert received.equals(Collections.singletonList(new StringMessage("hello")));
    }

    @Test
    public void parseCompositeJson() throws Exception {
        send("!{\"one\": \"uno\", \"and\": {\"two\": 2}, \"2\": [4, 2]}");

        assert received.get(0).read("one").equals(new StringMessage("uno"));
        assert received.get(0).read("and").read("two").equals(new IntegerMessage(2));
        assert received.get(0).read(2).read(0).equals(new IntegerMessage(4));
        assert received.get(0).read(2).read(1).equals(new IntegerMessage(2));
    }

    @Test
    public void parseShortSyntax() throws Exception {
        send("foo");
        assert received.get(0).read(0).equals(new StringMessage("foo"));

        send("42");
        assert received.get(1).read(0).equals(new IntegerMessage(42));

        send("yes");
        assert received.get(2).read(0).equals(new BooleanMessage(true));

        send("no");
        assert received.get(3).read(0).equals(new BooleanMessage(false));

        send("0xbaba");
        assert received.get(4).read(0).equals(BinaryMessage.fromString("baba"));

        send("foo bar");
        assert received.get(5).read(0).equals(new StringMessage("foo"));
        assert received.get(5).read(1).equals(new StringMessage("bar"));

        send("foo:bar");
        assert received.get(6).read("foo").equals(new StringMessage("bar"));

        send("foo bar:yes");
        assert received.get(7).read(0).equals(new StringMessage("foo"));
        assert received.get(7).read("bar").equals(new BooleanMessage(true));

        send("foo:bar foo:baz");
        assert received.get(8).read("foo").read(0).equals(new StringMessage("bar"));
        assert received.get(8).read("foo").read(1).equals(new StringMessage("baz"));

        send("foo:yes bar");
        assert received.get(9).read("foo").equals(new BooleanMessage(true));
        assert received.get(9).read(0).equals(new StringMessage("bar"));

        send("@0xbaba");
        assert received.get(10).read(0).equals(new AddressMessage(Address.fromString("baba")));

        book.put("foo", Address.fromString("dada"));
        send("@foo");
        assert received.get(11).read(0).equals(new AddressMessage(Address.fromString("dada")));
    }

    @Test
    public void parseQuotedSyntax() throws Exception {
        send("\"foo: bar\" foo:\"cat dog\"");
        assert received.get(0).read(0).equals(new StringMessage("foo: bar"));
        assert received.get(0).read("foo").equals(new StringMessage("cat dog"));

        send("with\\:\\ space");
        assert received.get(1).read(0).equals(new StringMessage("with: space"));

        send("with\\\"quote");
        assert received.get(2).read(0).equals(new StringMessage("with\"quote"));

        send("\"a \\\"quoted\\\" message\"");
        assert received.get(3).read(0).equals(new StringMessage("a \"quoted\" message"));
    }
}