package org.zells.dish.network.connecting.implementations.socket;

import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.Packet;
import org.zells.dish.network.connecting.PacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TcpSocketConnection implements Connection {

    private int packetId = 1;

    private DataOutputStream out;
    private DataInputStream in;

    private PacketHandler handler;
    private Socket socket;

    private Map<Integer, Packet> responses = new HashMap<Integer, Packet>();
    private boolean open = false;

    public static boolean loggingEnabled = false;
    private int logCounter = 0;

    public TcpSocketConnection(final Socket socket) {
        this.socket = socket;
    }

    public TcpSocketConnection open() {
        log("Open");
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        open = true;
        new Receiver().start();

        return this;
    }

    public void close() {
        open = false;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public void setHandler(PacketHandler handler) {
        this.handler = handler;
    }

    public Packet transmit(Packet packet) throws IOException {
        if (!open) {
            throw new IOException("Connection not open");
        }

        int id = packetId++;
        log("Send " + packet.getBytes().length + " @" + id);
        send(new Transmission(packet, id, false));

        return waitForResponse(id);
    }

    private void send(Transmission transmission) throws IOException {
        byte[] bytes = transmission.packet.getBytes();
        out.writeBoolean(transmission.isResponse);
        out.writeInt(transmission.id);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private Transmission receive() throws IOException {
        final boolean isResponse = in.readBoolean();
        final int id = in.readInt();
        int length = in.readInt();
        byte[] message = new byte[length];
        in.readFully(message, 0, message.length);

        return new Transmission(new Packet(message), id, isResponse);
    }

    private Packet waitForResponse(int id) {
        while (!responses.containsKey(id)) {
            log("Hold for " + id);
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }

        log("Got " + responses.get(id).getBytes().length + " @" + id);
        return responses.remove(id);
    }

    private void log(String message) {
        if (loggingEnabled) {
            System.out.println(logCounter++ + " [" + socket.getLocalPort() + ">" + socket.getPort() + "] " + message);
        }
    }

    private class Receiver extends Thread {
        public void run() {
            while (open) {
                try {
                    final Transmission transmission = receive();
                    log("Received " + transmission.packet.getBytes().length + " @" + transmission.id);

                    if (transmission.isResponse) {
                        responses.put(transmission.id, transmission.packet);
                    } else if (transmission.packet.getBytes().length > 0) {
                        new Responder(transmission).start();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    private class Responder extends Thread {

        private Transmission transmission;

        Responder(Transmission transmission) {
            this.transmission = transmission;
        }

        public void run() {
            try {
                Packet response = handler.handle(transmission.packet);

                log("Reply " + response.getBytes().length + " @" + transmission.id);
                send(transmission.response(response));
            } catch (IOException ignored) {
            }
        }
    }
}