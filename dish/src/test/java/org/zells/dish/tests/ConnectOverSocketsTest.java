package org.zells.dish.tests;

import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.network.connecting.implementations.TcpSocketConnection;
import org.zells.dish.network.connecting.implementations.TcpSocketServer;
import org.zells.dish.tests.fakes.FakeZell;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectOverSocketsTest {

    @Test
    public void twoDishes() throws IOException {
        Dish one = Dish.buildDefault();
        Dish two = Dish.buildDefault();

        FakeZell zellOne = new FakeZell();
        FakeZell zellTwo = new FakeZell();

        Address addressOne = one.add(zellOne);
        Address addressTwo = two.add(zellTwo);

        TcpSocketServer server = new TcpSocketServer(new ServerSocket(42422)).start(two);
        TcpSocketConnection connection = new TcpSocketConnection(new Socket("localhost", 42422)).open();

        one.join(connection);
        one.send(addressTwo, new StringMessage("two"));
        two.send(addressOne, new StringMessage("one"));

        assert zellTwo.received.toString().equals("two");
        assert zellOne.received.toString().equals("one");

        connection.close();
        server.stop();
    }

    @Test
    public void zellsDoesNotExist() throws IOException {
        Exception caught = null;

        Dish one = Dish.buildDefault();
        Dish two = Dish.buildDefault();

        TcpSocketServer server = new TcpSocketServer(new ServerSocket(42423)).start(two);
        TcpSocketConnection connection = new TcpSocketConnection(new Socket("localhost", 42423)).open();

        one.join(connection);
        try {
            one.send(Address.fromString("dada"), new StringMessage("two"));
        } catch (ReceiverNotFoundException e) {
            caught = e;
        }

        assert caught != null;

        connection.close();
        server.stop();
    }

    @Test
    public void threeDishes() throws IOException {
        Dish one = Dish.buildDefault();
        Dish two = Dish.buildDefault();
        Dish three = Dish.buildDefault();

        FakeZell zell = new FakeZell();
        Address address = one.add(zell);

        TcpSocketServer server = new TcpSocketServer(new ServerSocket(42424)).start(two);
        TcpSocketConnection connectionOne = new TcpSocketConnection(new Socket("localhost", 42424)).open();
        TcpSocketConnection connectionThree = new TcpSocketConnection(new Socket("localhost", 42424)).open();

        one.join(connectionOne);
        three.join(connectionThree);
        three.send(address, new StringMessage("one"));

        assert zell.received.toString().equals("one");

        connectionOne.close();
        connectionThree.close();
        server.stop();
    }
}
