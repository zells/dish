package org.zells.samples.tests;

import org.junit.Ignore;
import org.junit.Test;
import org.zells.dish.delivery.messages.BinaryMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

public class LobbyTest {

    @Test
    @Ignore("TBD")
    public void enter() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo"));
    }

    @Test
    @Ignore("TBD")
    public void introduce() {
        new CompositeMessage()
                .put(0, new StringMessage("hello"));
    }

    @Test
    @Ignore("TBD")
    public void alreadyThere() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("bar"));
    }

    @Test
    @Ignore("TBD")
    public void nameTaken() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("cece"))
                .put("as", new StringMessage("foo"));
    }

    @Test
    @Ignore("TBD")
    public void leave() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("dada"))
                .put("as", new StringMessage("foo"));
        new CompositeMessage()
                .put("leave", BinaryMessage.fromString("dada"));
    }

    @Test
    @Ignore("TBD")
    public void talkToAll() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("22"))
                .put("as", new StringMessage("bar"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("33"))
                .put("as", new StringMessage("baz"));
        new CompositeMessage()
                .put("say", new StringMessage("Hello World"));
    }

    @Test
    @Ignore("TBD")
    public void talkToSomebody() {
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("11"))
                .put("as", new StringMessage("foo"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("22"))
                .put("as", new StringMessage("bar"));
        new CompositeMessage()
                .put("enter", BinaryMessage.fromString("33"))
                .put("as", new StringMessage("baz"));
        new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("to", new StringMessage("bar"));
    }

    @Test
    @Ignore("TBD")
    public void listenToTopic() {
        new CompositeMessage()
                .put("inform", BinaryMessage.fromString("dada"))
                .put("about", new StringMessage("a topic"));
        new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("regarding", new StringMessage("a topic"));
    }

    @Test
    @Ignore("TBD")
    public void ignoreTopic() {
        new CompositeMessage()
                .put("listen", BinaryMessage.fromString("dada"))
                .put("to", new StringMessage("a topic"));
        new CompositeMessage()
                .put("listen", BinaryMessage.fromString("fade"))
                .put("to", new StringMessage("a topic"));
        new CompositeMessage()
                .put("spare", BinaryMessage.fromString("dada"))
                .put("about", new StringMessage("a topic"));
        new CompositeMessage()
                .put("say", new StringMessage("Hello World"))
                .put("regarding", new StringMessage("a topic"));
    }
}
