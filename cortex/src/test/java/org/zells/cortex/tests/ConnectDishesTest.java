package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.cortex.Cortex;
import org.zells.cortex.tests.fakes.FakeConnection;
import org.zells.cortex.tests.fakes.FakeConnectionRepository;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.ConnectionRepository;
import org.zells.dish.network.connecting.Server;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectDishesTest {

    private Dish dish;

    private Address cortex;
    private List<Integer> stopped = new ArrayList<Integer>();
    private List<Integer> started = new ArrayList<Integer>();
    private List<Connection> joined = new ArrayList<Connection>();
    private List<Connection> left = new ArrayList<Connection>();

    @Before
    public void SetUp() {
        dish = new Dish(new BasicUuidGenerator(), new EncodingRepository()) {
            @Override
            public void join(Connection connection) {
                joined.add(connection);
            }

            @Override
            public void leave(Connection connection) {
                left.add(connection);
            }
        };
        Cortex cortex = new Cortex(dish, new FakeConnectionRepository()
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
    public void joinPeer() {
        send(new CompositeMessage(new StringMessage("join"))
                .put("host", new StringMessage("foo"))
                .put("port", new IntegerMessage(12345)));
        assert joined.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    public void joinPeerDefaultHost() {
        send(new CompositeMessage(new StringMessage("join"))
                .put("port", new IntegerMessage(12345)));
        assert joined.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    public void voidJoinPeerDefaultPort() {
        send(new CompositeMessage(new StringMessage("join"))
                .put("host", new StringMessage("foo")));
        assert joined.contains(new FakeConnection("tcp:foo:42420"));
    }

    @Test
    public void leavePeer() {
        send(new CompositeMessage(new StringMessage("leave"))
                .put("host", new StringMessage("foo"))
                .put("port", new IntegerMessage(12345)));
        assert left.contains(new FakeConnection("tcp:foo:12345"));
    }

    @Test
    public void leavePeerDefaultHost() {
        send(new CompositeMessage(new StringMessage("leave"))
                .put("port", new IntegerMessage(12345)));
        assert left.contains(new FakeConnection("tcp:localhost:12345"));
    }

    @Test
    public void leavePeerDefaultPort() {
        send(new CompositeMessage(new StringMessage("leave"))
                .put("host", new StringMessage("foo")));
        assert left.contains(new FakeConnection("tcp:foo:42420"));
    }

    private void send(CompositeMessage message) {
        dish.send(cortex, message).sync();
    }
}
