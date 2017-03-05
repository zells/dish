package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeConnectionRepository;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

public class ReferenceMessagesTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();
        new Client(dish, user, new FakeConnectionRepository());

        user.hear("client listen as:me");
    }

    @Test
    public void notAReference() {
        user.hear("a0 \"#0\"");

        assert dish.lastMessage.read(0).equals(new StringMessage("#0"));
    }

    @Test
    public void invalidReference() {
        user.hear("a0 #0");

        assert user.told.contains("Parsing error: Invalid reference: 0");
    }

    @Test
    public void wholeMessage() {
        user.hear("me !42");
        user.hear("a0 #0");

        assert user.told.contains("0> 42");
        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
    }

    @Test
    public void mixedContent() {
        user.hear("me !42");
        user.hear("a0 foo #0 bar");

        assert dish.lastMessage.read(0).equals(new StringMessage("foo"));
        assert dish.lastMessage.read(1).equals(new IntegerMessage(42));
        assert dish.lastMessage.read(2).equals(new StringMessage("bar"));
    }

    @Test
    public void partOfMessage() {
        user.hear("me 42 foo:bar");
        user.hear("a0 #0.0 #0.foo");

        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
        assert dish.lastMessage.read(1).equals(new StringMessage("bar"));
    }

    @Test
    public void deepReference() {
        user.hear("me !{\"foo\":{\"bar\":[1, 42]}}");
        user.hear("a0 #0.foo.bar.1");

        assert dish.lastMessage.read(0).equals(new IntegerMessage(42));
    }

    @Test
    public void useReferenceAsReceiver() {
        user.hear("me 0xdada");
        user.hear("#0.0 foo");

        assert dish.sent.get(2).getKey().equals(Address.fromString("dada"));
    }
}
