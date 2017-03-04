package org.zells.dish;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Delivery;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.PacketHandler;
import org.zells.dish.network.Peer;
import org.zells.dish.network.SignalListener;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.BasicUuidGenerator;
import org.zells.dish.util.UuidGenerator;

import java.util.*;

public class Dish {

    private UuidGenerator generator;
    private EncodingRepository encodings;

    private Map<Address, Zell> culture = new HashMap<Address, Zell>();
    private Map<Connection, Peer> peers = new IdentityHashMap<Connection, Peer>();
    private Set<Delivery> delivered = new HashSet<Delivery>();

    public Dish(UuidGenerator generator, EncodingRepository encodings) {
        this.generator = generator;
        this.encodings = encodings;
    }

    public static Dish buildDefault() {
        EncodingRepository encodings = new EncodingRepository().addAll(EncodingRepository.supportedEncodings());
        BasicUuidGenerator generator = new BasicUuidGenerator();

        return new Dish(generator, encodings);
    }

    public void send(Address receiver, Message message) {
        deliver(new Delivery(generator.generate(), receiver, message));
    }

    private void deliver(Delivery delivery) {
        boolean delivered = !alreadyDelivered(delivery)
                && (deliverLocally(delivery)
                || deliverRemotely(delivery));

        if (!delivered) {
            throw new ReceiverNotFoundException(delivery);
        }
    }

    private boolean alreadyDelivered(Delivery delivery) {
        if (delivered.contains(delivery)) {
            return true;
        }
        delivered.add(delivery);
        return false;
    }

    private boolean deliverLocally(Delivery delivery) {
        if (!culture.containsKey(delivery.getReceiver())) {
            return false;
        }
        culture.get(delivery.getReceiver()).receive(delivery.getMessage());
        return true;
    }

    private boolean deliverRemotely(Delivery delivery) {
        for (Peer peer : peers.values()) {
            if (peer.deliver(delivery)) {
                return true;
            }
        }
        return false;
    }

    public Address add(Zell zell) {
        Address address = new Address(generator.generate());
        culture.put(address, zell);
        return address;
    }

    public void join(Connection connection) {
        listen(connection);
        connect(connection).join();
    }

    public void leave(Connection connection) {
        disconnect(connection).leave();
    }

    public void leaveAll() {
        for (Peer peer : peers.values()) {
            peer.leave();
        }
        peers.clear();
    }

    private Peer connect(Connection connection) {
        Peer peer = new Peer(encodings, connection);
        peers.put(connection, peer);
        return peer;
    }

    private Peer disconnect(Connection connection) {
        return peers.remove(connection);
    }

    public void listen(Connection connection) {
        connection.setHandler(new PacketHandler(encodings, connection, new DishSignalListener()));
    }

    private class DishSignalListener implements SignalListener {

        public void onDeliver(Delivery delivery) {
            deliver(delivery);
        }

        public void onJoin(Connection connection) {
            connect(connection);
        }

        public void onLeave(Connection connection) {
            disconnect(connection);
        }
    }
}
