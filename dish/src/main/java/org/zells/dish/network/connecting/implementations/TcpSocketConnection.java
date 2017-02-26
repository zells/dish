package org.zells.dish.network.connecting.implementations;

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

    private static int packetId = 0;

    private DataOutputStream out;
    private DataInputStream in;

    private PacketHandler handler;
    private Socket socket;

    private Map<Integer, Packet> buffer = new HashMap<Integer, Packet>();
    private boolean open = false;

    public TcpSocketConnection(final Socket socket) {
        this.socket = socket;
    }

    public TcpSocketConnection open() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        open = true;
        new Thread(new Runnable() {
            public void run() {
                while (open) {
                    try {
                        final int id = in.readInt();
                        if (id == 0) {
                            continue;
                        }
                        packetId = id + 1;
                        int length = in.readInt();
                        if (length == 0) {
                            continue;
                        }

                        byte[] message = new byte[length];
                        in.readFully(message, 0, message.length);

                        final Packet packet = new Packet(message);
                        buffer.put(id, packet);

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Packet response = handler.handle(packet);

                                    byte[] bytes = response.getBytes();
                                    out.writeInt(id);
                                    out.writeInt(bytes.length);
                                    out.write(bytes);
                                } catch (IOException ignored) {
                                }
                            }
                        }).start();
                    } catch (IOException ignored) {
                    }
                }
            }
        }).

                start();

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

        packetId++;
        int id = packetId;

        byte[] bytes = packet.getBytes();
        out.writeInt(id);
        out.writeInt(bytes.length);
        out.write(bytes);

        while (!buffer.containsKey(id)) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }

        return buffer.remove(id);
    }
}