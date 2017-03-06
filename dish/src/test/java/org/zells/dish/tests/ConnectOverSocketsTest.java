package org.zells.dish.tests;

import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketConnection;
import org.zells.dish.network.connecting.implementations.socket.TcpSocketServer;
import org.zells.dish.tests.fakes.FakeZell;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectOverSocketsTest {

    private int executed = 0;

    @Test
    public void twoDishes() throws Exception {
        Dish one = Dish.buildDefault();
        Dish two = Dish.buildDefault();

        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = one.add(zellOne);
        Address addressTwo = two.add(zellTwo);

        TcpSocketServer server = new TcpSocketServer(new ServerSocket(42422)).start(two);
        TcpSocketConnection connection = new TcpSocketConnection(new Socket("localhost", 42422)).open();

        one.join(connection);
        increase(one.send(addressTwo, new StringMessage("two")));
        increase(two.send(addressOne, new StringMessage("one")));

        waitFor(2);
        assert zellTwo.received.toString().equals("two");
        assert zellOne.received.toString().equals("one");

        connection.close();
        server.stop();
    }

    @Test
    public void receiverDoesNotExist() throws IOException {
        final Exception[] caught = {null};

        Dish one = Dish.buildDefault();
        Dish two = Dish.buildDefault();

        TcpSocketServer server = new TcpSocketServer(new ServerSocket(42423)).start(two);
        TcpSocketConnection connection = new TcpSocketConnection(new Socket("localhost", 42423)).open();

        one.join(connection);
        one.send(Address.fromString("dada"), new StringMessage("two"))
                .when(new Messenger.Failed() {
                    public void then(Exception e) {
                        caught[0] = e;
                        executed++;
                    }
                });

        waitFor(1);
        assert caught[0] instanceof ReceiverNotFoundException;

        connection.close();
        server.stop();
    }

    @Test
    public void proxyDish() throws IOException {
        Dish one = Dish.buildDefault();
        Dish proxy = Dish.buildDefault();
        Dish two = Dish.buildDefault();

        FakeZell zellOne = new FakeZell();
        Address addressOne = one.add(zellOne);
        FakeZell zellTwo = new FakeZell();
        Address addressTwo = two.add(zellTwo);

        TcpSocketServer proxyServer = new TcpSocketServer(new ServerSocket(42424)).start(proxy);
        TcpSocketConnection connectionOne = new TcpSocketConnection(new Socket("localhost", 42424)).open();
        TcpSocketConnection connectionTwo = new TcpSocketConnection(new Socket("localhost", 42424)).open();

        one.join(connectionOne);
        two.join(connectionTwo);

        increase(one.send(addressTwo, new StringMessage("a")));
        increase(two.send(addressOne, new StringMessage("aa")));
        increase(one.send(addressTwo, new StringMessage("aaa")));
        increase(one.send(addressTwo, new StringMessage("aaaa")));
        increase(one.send(addressTwo, new StringMessage("aaaaa")));

        waitFor(5);
        connectionOne.close();
        connectionTwo.close();
        proxyServer.stop();
    }

    private Messenger increase(Messenger messenger) {
        return messenger.when(new Messenger.Delivered() {
            public void then() {
                executed++;
            }
        });
    }

    private void waitFor(int i) {
        long start = System.currentTimeMillis();
        while (executed < i) {
            Thread.yield();
            assert System.currentTimeMillis() - start < 4000;
        }
    }
}
