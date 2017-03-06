package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeConnectionRepository;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.*;

public class SendMessagesTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();

        new Client(dish, user, new FakeConnectionRepository());
    }

    @Test
    public void invalidAddress() {
        user.hear("not");

        assert dish.sent.isEmpty();
        assert user.told.contains("Parsing error: Invalid hex string: not");
    }

    @Test
    public void sendNothing() {
        user.hear("0xfade");
        user.hear("fade");

        assert dish.sent.get(0).getKey().equals(Address.fromString("fade"));
        assert dish.sent.get(1).getKey().equals(Address.fromString("fade"));
        assert dish.sent.get(0).getValue().equals(new NullMessage());
    }

    @Test
    public void invalidMessage() {
        user.hear("fade !invalid");

        assert dish.sent.isEmpty();
        assert user.told.get(0).startsWith("Parsing error: Unrecognized token 'invalid'");
    }

    @Test
    public void parseScalarJson() {
        user.hear("d0 !null");
        assert dish.sent.get(0).getValue().equals(new NullMessage());

        user.hear("d1   !\"foo\"");
        assert dish.sent.get(1).getValue().equals(new StringMessage("foo"));

        user.hear("d2 !42  ");
        assert dish.sent.get(2).getValue().equals(new IntegerMessage(42));

        user.hear("d3 !true");
        assert dish.sent.get(3).getValue().equals(new BooleanMessage(true));

        user.hear("d4 !false");
        assert dish.sent.get(4).getValue().equals(new BooleanMessage(false));

        user.hear("d5 !\"0xbaba\"");
        assert dish.sent.get(5).getValue().equals(BinaryMessage.fromString("baba"));
    }

    @Test
    public void sendWithArrow() {
        user.hear("fade < !\"hello\"");
        assert dish.lastMessage.equals(new StringMessage("hello"));
    }

    @Test
    public void parseCompositeJson() {
        user.hear("da !{\"one\": \"uno\", \"and\": {\"two\": 2}, \"2\": [4, 2]}");

        assert dish.sent.get(0).getValue().read("one").equals(new StringMessage("uno"));
        assert dish.sent.get(0).getValue().read("and").read("two").equals(new IntegerMessage(2));
        assert dish.sent.get(0).getValue().read(2).read(0).equals(new IntegerMessage(4));
        assert dish.sent.get(0).getValue().read(2).read(1).equals(new IntegerMessage(2));
    }

    @Test
    public void parseShortSyntax() {
        user.hear("d0 foo");
        assert dish.sent.get(0).getValue().read(0).equals(new StringMessage("foo"));

        user.hear("d1 42");
        assert dish.sent.get(1).getValue().read(0).equals(new IntegerMessage(42));

        user.hear("d2 yes");
        assert dish.sent.get(2).getValue().read(0).equals(new BooleanMessage(true));

        user.hear("d3 no");
        assert dish.sent.get(3).getValue().read(0).equals(new BooleanMessage(false));

        user.hear("d4 0xbaba");
        assert dish.sent.get(4).getValue().read(0).equals(BinaryMessage.fromString("baba"));

        user.hear("d5 foo bar");
        assert dish.sent.get(5).getValue().read(0).equals(new StringMessage("foo"));
        assert dish.sent.get(5).getValue().read(1).equals(new StringMessage("bar"));

        user.hear("d6 foo:bar");
        assert dish.sent.get(6).getValue().read("foo").equals(new StringMessage("bar"));

        user.hear("d7 foo bar:yes");
        assert dish.sent.get(7).getValue().read(0).equals(new StringMessage("foo"));
        assert dish.sent.get(7).getValue().read("bar").equals(new BooleanMessage(true));

        user.hear("d8 foo:bar foo:baz");
        assert dish.sent.get(8).getValue().read("foo").read(0).equals(new StringMessage("bar"));
        assert dish.sent.get(8).getValue().read("foo").read(1).equals(new StringMessage("baz"));

        user.hear("d7 foo:yes bar");
        assert dish.sent.get(9).getValue().read("foo").equals(new BooleanMessage(true));
        assert dish.sent.get(9).getValue().read(0).equals(new StringMessage("bar"));
    }

    @Test
    public void parseQuotedSyntax() {
        user.hear("d0 \"foo: bar\" foo:\"cat dog\"");
        assert dish.sent.get(0).getValue().read(0).equals(new StringMessage("foo: bar"));
        assert dish.sent.get(0).getValue().read("foo").equals(new StringMessage("cat dog"));

        user.hear("d1 with\\:\\ space");
        assert dish.sent.get(1).getValue().read(0).equals(new StringMessage("with: space"));

        user.hear("d2 with\\\"quote");
        assert dish.sent.get(2).getValue().read(0).equals(new StringMessage("with\"quote"));

        user.hear("d3 \"a \\\"quoted\\\" message\"");
        assert dish.sent.get(3).getValue().read(0).equals(new StringMessage("a \"quoted\" message"));
    }
}