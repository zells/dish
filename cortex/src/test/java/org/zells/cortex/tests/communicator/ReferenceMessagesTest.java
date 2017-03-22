package org.zells.cortex.tests.communicator;

import org.junit.Ignore;
import org.junit.Test;

public class ReferenceMessagesTest {

    @Test
    @Ignore
    public void notAReference() {
//        user.hear("a0 \"#0\"");
//
//        assert dish.lastMessage.read(0).equals(new StringMessage("#0"));
    }

    @Test
    @Ignore
    public void invalidReference() {
//        user.hear("a0 #0");
//
//        assert user.told.contains("Parsing error: Invalid reference: 0");
    }

    @Test
    @Ignore
    public void wholeMessage() {
//        user.hear("me !42");
//        user.hear("a0 #0");
//
//        assert user.told.contains("0> 42");
//        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
    }

    @Test
    @Ignore
    public void mixedContent() {
//        user.hear("me !42");
//        user.hear("a0 foo #0 bar");
//
//        assert dish.lastMessage.read(0).equals(new StringMessage("foo"));
//        assert dish.lastMessage.read(1).equals(new IntegerMessage(42));
//        assert dish.lastMessage.read(2).equals(new StringMessage("bar"));
    }

    @Test
    @Ignore
    public void partOfMessage() {
//        user.hear("me 42 foo:bar");
//        user.hear("a0 #0.0 #0.foo");
//
//        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
//        assert dish.lastMessage.read(1).equals(new StringMessage("bar"));
    }

    @Test
    @Ignore
    public void deepReference() {
//        user.hear("me !{\"foo\":{\"bar\":[1, 42]}}");
//        user.hear("a0 #0.foo.bar.1");
//
//        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
    }

    @Test
    @Ignore
    public void useReferenceAsReceiver() {
//        user.hear("me 0xdada");
//        user.hear("#0.0 foo");
//
//        assert dish.sent.get(2).getKey().equals(Address.fromString("dada"));
    }
}
