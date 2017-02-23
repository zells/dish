package org.zells.dish.tests;

import org.junit.Test;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.StringMessage;
import org.zells.dish.tests.fakes.FakeUuidGenerator;
import org.zells.dish.tests.fakes.FakeZell;
import org.zells.dish.network.ConnectionRepository;
import org.zells.dish.network.connections.TcpSocketServer;
import org.zells.dish.network.encoding.EncodingRepository;

public class ConnectOverSocketsTest {

    @Test
    public void singleConnection() {
        FakeUuidGenerator generator = new FakeUuidGenerator();
        EncodingRepository encodings = new EncodingRepository().addAll(EncodingRepository.supportedEncodings());
        ConnectionRepository connections = new ConnectionRepository().addAll(ConnectionRepository.supportedConnections());

        TcpSocketServer serverOne = new TcpSocketServer("localhost", 42421, encodings);
        Dish one = new Dish(serverOne, generator, encodings, connections);

        TcpSocketServer serverTwo = new TcpSocketServer("localhost", 42422, encodings);
        Dish two = new Dish(serverTwo, generator, encodings, connections);

        FakeZell aZell = new FakeZell();
        Address anAddress = two.add(aZell);

        one.join(serverTwo.getConnectionDescription());
        one.send(anAddress, new StringMessage("hello"));

        serverOne.stop();
        serverTwo.stop();

        assert aZell.received.asString().equals("hello");
    }
}
