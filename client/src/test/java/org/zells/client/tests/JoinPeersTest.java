package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;

public class JoinPeersTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();

        FakeDish.nextAddress = "fade";
        new Client(dish, user);
    }

    @Test
    public void joinPeerDefaultHost() {
        user.hear("fade join port:12345");
        assert dish.joined.contains("tcp:localhost:12345");
    }

    @Test
    public void leavePeerDefaultHost() {
        user.hear("fade leave port:12345");
        assert dish.left.contains("tcp:localhost:12345");
    }

    @Test
    public void exit() {
        user.hear("fade exit");
        assert dish.stopped;
        assert user.stopped;
    }
}
