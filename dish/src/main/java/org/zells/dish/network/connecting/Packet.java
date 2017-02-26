package org.zells.dish.network.connecting;

public class Packet {

    private byte[] bytes;

    public Packet(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
