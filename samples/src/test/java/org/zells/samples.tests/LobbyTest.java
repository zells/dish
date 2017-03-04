package org.zells.samples.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.BinaryMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.samples.LobbyZell;
import org.zells.samples.tests.fakes.FakeDish;

import java.util.AbstractMap;

public class LobbyTest {

    private FakeDish dish;
    private LobbyZell lobby;

    @Before
    public void setUp() {
        dish = new FakeDish();
        lobby = new LobbyZell(dish);
    }

    @Test
    public void introduce() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("abcd"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("1234"))
                .put("as", new StringMessage("bar")));
        send(new CompositeMessage()
                .put("hello", BinaryMessage.fromString("fade")));

        assertSent("fade", "Currently here: [bar, foo]");
    }

    @Test
    public void alreadyThere() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("bar")));

        assertSent("dada", "You are already there as [foo]");
    }

    @Test
    public void nameTaken() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("cece"))
                .put("as", new StringMessage("foo")));

        assertSent("cece", "There is already somebody here with that name.");
    }

    @Test
    public void leave() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("leave", BinaryMessage.fromString("dada")));

        assertSent("dada", "Good-bye");

        send(new CompositeMessage()
                .put("hello", BinaryMessage.fromString("fade")));

        assertSent("fade", "Currently here: []");
    }

    @Test
    public void talkToAll() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("22"))
                .put("as", new StringMessage("bar")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("33"))
                .put("as", new StringMessage("baz")));
        send(new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("as", BinaryMessage.fromString("11")));

        assert dish.sent.size() == 2;
        assertSent("22", new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("from", new StringMessage("foo")));
        assertSent("33", new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("from", new StringMessage("foo")));
    }

    @Test
    public void talkToSomebody() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("22"))
                .put("as", new StringMessage("bar")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("33"))
                .put("as", new StringMessage("baz")));
        send(new CompositeMessage()
                .put("say", new StringMessage("Hello You"))
                .put("to", new StringMessage("bar"))
                .put("as", BinaryMessage.fromString("11")));

        assert dish.sent.size() == 1;
        assertSent("22", new CompositeMessage()
                .put("message", new StringMessage("Hello You"))
                .put("from", new StringMessage("foo")));
    }

    @Test
    public void listenToTopic() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("inform", BinaryMessage.fromString("dada"))
                .put("about", new StringMessage("a topic")));
        send(new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("as", BinaryMessage.fromString("11"))
                .put("regarding", new StringMessage("a topic")));

        assert dish.sent.size() == 1;
        assertSent("dada", new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("regarding", new StringMessage("a topic"))
                .put("from", new StringMessage("foo")));
    }

    @Test
    public void nobodyCares() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("22"))
                .put("as", new StringMessage("bar")));
        send(new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("as", BinaryMessage.fromString("11"))
                .put("regarding", new StringMessage("a topic")));

        assert dish.sent.size() == 0;
    }

    @Test
    public void ignoreTopic() {
        send(new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo")));
        send(new CompositeMessage()
                .put("inform", BinaryMessage.fromString("dada"))
                .put("about", new StringMessage("a topic")));
        send(new CompositeMessage()
                .put("inform", BinaryMessage.fromString("fade"))
                .put("about", new StringMessage("a topic")));
        send(new CompositeMessage()
                .put("spare", BinaryMessage.fromString("dada"))
                .put("about", new StringMessage("a topic")));
        send(new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("as", BinaryMessage.fromString("11"))
                .put("regarding", new StringMessage("a topic")));

        assert dish.sent.size() == 1;
        assertSent("fade", new CompositeMessage()
                .put("message", new StringMessage("Hello World"))
                .put("regarding", new StringMessage("a topic"))
                .put("from", new StringMessage("foo")));
    }

    private void assertSent(String to, String message) {
        assertSent(to, new StringMessage(message));
    }

    private void assertSent(String to, Message message) {
        assert dish.sent.contains(new
                AbstractMap.SimpleEntry<Address, Message>(Address.fromString(to), message));
    }

    private void send(Message message) {
        lobby.receive(message);
    }
}
