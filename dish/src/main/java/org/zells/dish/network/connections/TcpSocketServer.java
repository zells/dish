package org.zells.dish.network.connections;

import org.zells.dish.network.Packet;
import org.zells.dish.network.Server;
import org.zells.dish.network.Signal;
import org.zells.dish.network.SignalListener;
import org.zells.dish.network.encoding.EncodingRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSocketServer implements Server {

    private final String host;
    private final int port;
    private EncodingRepository encodings;

    private boolean running;
    private ServerSocket server;

    public TcpSocketServer(String host, int port, EncodingRepository encodings) {
        this.host = host;
        this.port = port;
        this.encodings = encodings;
    }

    public void start(SignalListener listener) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open port " + port, e);
        }

        running = true;

        (new SocketListener(server, listener)).start();
    }

    public void stop() {
        running = false;

        if (server == null) {
            return;
        }

        try {
            server.close();
        } catch (IOException ignored) {
        }
    }

    public String getConnectionDescription() {
        return "tcp:" + host + ":" + port;
    }

    private class SocketListener extends Thread {

        private ServerSocket server;
        private SignalListener listener;

        SocketListener(ServerSocket server, SignalListener listener) {
            this.server = server;
            this.listener = listener;
        }

        @Override
        public void run() {
            new Thread(new Runnable() {
                public void run() {
                    while (running) {
                        try {
                            Socket socket = server.accept();
                            (new SignalWorker(socket, listener)).start();
                        } catch (IOException e) {
                            if (running) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }).start();
        }
    }

    private class SignalWorker extends Thread {

        private final Socket socket;
        private final SignalListener listener;

        SignalWorker(Socket socket, SignalListener listener) {
            this.socket = socket;
            this.listener = listener;
        }

        @Override
        public void run() {
            DataOutputStream out = null;
            DataInputStream in = null;

            try {
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());


                int length = in.readInt();
                if (length > 0) {
                    byte[] message = new byte[length];
                    in.readFully(message, 0, message.length);
                    Signal receivedSignal = encodings.decode(new Packet(message));
                    Signal responseSignal = listener.respondTo(receivedSignal);
                    Packet response = encodings.encode(responseSignal);

                    byte[] bytes = response.getBytes();
                    out.writeInt(bytes.length);
                    out.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (in != null) in.close();
                if (out != null) out.close();
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
