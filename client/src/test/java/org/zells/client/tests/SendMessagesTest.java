package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
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

        new Client(user, dish);
    }

    @Test
    public void invalidAddress() {
        user.hear("not");

        assert dish.sent.isEmpty();
        assert user.told.get(1).equals("Parsing error: Invalid hex string: not");
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
        assert user.told.get(1).startsWith("Parsing error: Unrecognized token 'invalid'");
    }

    @Test
    public void sendSimpleMessages() {
        user.hear("da !null");
        user.hear("da   !\"foo\"");
        user.hear("da !42  ");
        user.hear("da !true");
        user.hear("da !false");
        user.hear("da !\"0xbaba\"");

        assert dish.sent.get(0).getValue().equals(new NullMessage());
        assert dish.sent.get(1).getValue().equals(new StringMessage("foo"));
        assert dish.sent.get(2).getValue().equals(new IntegerMessage(42));
        assert dish.sent.get(3).getValue().equals(new BooleanMessage(true));
        assert dish.sent.get(4).getValue().equals(new BooleanMessage(false));
        assert dish.sent.get(5).getValue().equals(BinaryMessage.fromString("baba"));
    }

    @Test
    public void sendComplexMessage() {
        user.hear("da !{\"one\": \"uno\", \"and\": {\"two\": 2}, \"2\": [4, 2]}");

        assert dish.sent.get(0).getValue().read("one").equals(new StringMessage("uno"));
        assert dish.sent.get(0).getValue().read("and").read("two").equals(new IntegerMessage(2));
        assert dish.sent.get(0).getValue().read(2).read(0).equals(new IntegerMessage(4));
        assert dish.sent.get(0).getValue().read(2).read(1).equals(new IntegerMessage(2));
    }
}
