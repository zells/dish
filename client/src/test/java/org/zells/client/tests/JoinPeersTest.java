package org.zells.client.tests;

import com.sun.org.apache.bcel.internal.generic.FADD;
import org.junit.Before;
import org.junit.Ignore;
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
        new Client(user, dish);
    }

    @Test
    public void introducesYourself() {
        assert user.told.contains("Hi. I am 0xfade");
    }

    @Test
    public void connectPeerDefaultHost() {
        user.hear("fade connect port:12345");
        assert dish.connected.contains("tcp:localhost:12345");
    }

    @Test
    public void joinPeerDefaultHost() {
        user.hear("fade join port:12345");
        assert dish.joined.contains("tcp:localhost:12345");
    }
}
