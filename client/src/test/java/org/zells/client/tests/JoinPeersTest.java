package org.zells.client.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.client.Client;
import org.zells.client.tests.fakes.FakeConnection;
import org.zells.client.tests.fakes.FakeConnectionRepository;
import org.zells.client.tests.fakes.FakeDish;
import org.zells.client.tests.fakes.FakeUser;
import org.zells.dish.network.connecting.implementations.NullServer;

public class JoinPeersTest {

    private FakeUser user;
    private FakeDish dish;

    @Before
    public void SetUp() {
        user = new FakeUser();
        dish = new FakeDish();

        FakeDish.nextAddress = "fade";
        new Client(dish, new NullServer(), user, new FakeConnectionRepository());
    }

    @Test
    public void joinPeer() {
        user.hear("fade join host:foo port:12345");
        assert dish.joined.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    public void leavePeer() {
        user.hear("fade leave host:foo port:12345");
        assert dish.left.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    public void joinPeerDefaultHost() {
        user.hear("fade join port:12345");
        assert dish.joined.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    public void leavePeerDefaultHost() {
        user.hear("fade leave port:12345");
        assert dish.left.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    public void voidJoinPeerDefaultPort() {
        user.hear("fade join host:foo");
        assert dish.joined.contains(new FakeConnection("tcp:foo:42420"));
    }

    @Test
    public void leavePeerDefaultPort() {
        user.hear("fade leave host:foo");
        assert dish.left.contains(new FakeConnection("tcp:foo:42420"));
    }

    @Test
    public void exit() {
        user.hear("fade exit");
        assert dish.leftAll;
        assert user.stopped;
    }
}
