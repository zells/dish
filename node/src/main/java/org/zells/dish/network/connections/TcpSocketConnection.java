package org.zells.dish.network.connections;

import org.zells.dish.network.Connection;
import org.zells.dish.network.ConnectionFactory;
import org.zells.dish.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpSocketConnection implements Connection {

    private final String host;
    private final int port;

    TcpSocketConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Packet transmit(Packet packet) throws IOException {
            Socket socket = new Socket(host, port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            byte[] bytes = packet.getBytes();
            out.writeInt(bytes.length);
            out.write(bytes);

            Packet response = new Packet(new byte[0]);

            int length = in.readInt();
            if (length > 0) {
                byte[] message = new byte[length];
                in.readFully(message, 0, message.length);
                response = new Packet(message);
            }

            out.close();
            in.close();
            socket.close();

            return response;
    }

    public static class Factory implements ConnectionFactory {

        public boolean canBuild(String description) {
            return description.startsWith("tcp:");
        }

        public Connection build(String description) {
            String[] hostPort = description.substring(4).split(":");
            return new TcpSocketConnection(hostPort[0], Integer.parseInt(hostPort[1]));
        }
    }
}
