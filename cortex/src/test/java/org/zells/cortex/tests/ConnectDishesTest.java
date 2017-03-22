package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.zells.cortex.Cortex;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectDishesTest {

    private Dish dish;

    private List<Integer> started = new ArrayList<Integer>();
    private List<Integer> stopped = new ArrayList<Integer>();
    private Address cortex;

    @Before
    public void SetUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository());
        Cortex cortex = new Cortex(dish, new ConnectionRepository()
                .setServerFactory(new ConnectionRepository.ServerFactory() {
                    @Override
                    public Server build(final int port) {
                        return new Server() {
                            @Override
                            public Server start(Dish dish) {
                                started.add(port);
                                return this;
                            }

                            @Override
                            public void stop() {
                                stopped.add(port);
                            }
                        };
                    }
                }));

        this.cortex = cortex.book.get("cortex");
    }

    @Test
    public void listenOnPort() {
        CompositeMessage message = new CompositeMessage().put("listen", new IntegerMessage(42102));
        send(message);

        assert started.equals(Collections.singletonList(42102));
    }

    @Test
    public void stopListening() {
        send(new CompositeMessage().put("listen", new IntegerMessage(42102)));
        send(new CompositeMessage().put("listen", new IntegerMessage(42101)));
        send(new CompositeMessage().put("stop", new IntegerMessage(42102)));

        assert stopped.equals(Collections.singletonList(42102));
    }

    @Test
    @Ignore
    public void joinPeer() {
//        user.hear("fade join host:foo port:12345");
//        assert dish.joined.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    @Ignore
    public void leavePeer() {
//        user.hear("fade leave host:foo port:12345");
//        assert dish.left.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    @Ignore
    public void joinPeerDefaultHost() {
//        user.hear("fade join port:12345");
//        assert dish.joined.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    @Ignore
    public void leavePeerDefaultHost() {
//        user.hear("fade leave port:12345");
//        assert dish.left.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    @Ignore
    public void voidJoinPeerDefaultPort() {
//        user.hear("fade join host:foo");
//        assert dish.joined.contains(new FakeConnection("tcp:foo:42420"));
    }

    @Test
    @Ignore
    public void leavePeerDefaultPort() {
//        user.hear("fade leave host:foo");
//        assert dish.left.contains(new FakeConnection("tcp:foo:42420"));
    }

    @Test
    @Ignore
    public void exit() {
//        user.hear("fade stop");
//        assert dish.leftAll;
//        assert user.stopped;
    }

    private void send(CompositeMessage message) {
        dish.send(cortex, message).sync();
    }
}
