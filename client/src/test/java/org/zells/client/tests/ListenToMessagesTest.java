package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;

public class ListenToMessagesTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();

        FakeDish.nextAddress = "fade";
        new Client(user, dish);
    }

    @Test
    public void addListener() {
        user.hear("fade listen");
        assert user.told.contains("Listening on 0xa3");
    }

    @Test
    public void tellReceivedMessages() {
        user.hear("fade listen");
        user.hear("0xa3 hello:world");
        assert user.told.contains(">>> {hello=world}");
    }
}
