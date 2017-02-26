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
        new Thread(new Runnable() {
            public void run() {
                while (open) {
                    try {
                        final boolean isResponse = in.readBoolean();
                        final int id = in.readInt();
                        int length = in.readInt();
                        if (length == 0) {
                            continue;
                        }
                        log("Received " + length + " @" + id);

                        byte[] message = new byte[length];
                        in.readFully(message, 0, message.length);

                        final Packet packet = new Packet(message);

                        if (isResponse) {
                            responses.put(id, packet);
                            continue;
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Packet response = handler.handle(packet);

                                    log("Reply " + response.getBytes().length + " @" + id);
                                    byte[] bytes = response.getBytes();
                                    out.writeBoolean(true);
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

        int id = packetId++;

        log("Send " + packet.getBytes().length + " @" + id);
        byte[] bytes = packet.getBytes();
        out.writeBoolean(false);
        out.writeInt(id);
        out.writeInt(bytes.length);
        out.write(bytes);

        return waitForResponse(id);
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
}