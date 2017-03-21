package org.zells.dish;

import org.zells.dish.delivery.*;
import org.zells.dish.network.Peer;
import org.zells.dish.network.SignalListener;
import org.zells.dish.network.connecting.Connection;
import org.zells.dish.network.connecting.PacketHandler;
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

    public Messenger send(final Address receiver, final Message message) {
        return new Messenger(new Runnable() {
            public void run() {
                Delivery delivery = new Delivery(generator.generate(), receiver, message);
                if (!deliver(delivery)) {
                    throw new ReceiverNotFoundException(delivery);
                }
            }
        });
    }

    private boolean deliver(Delivery delivery) {
         return !alreadyDelivered(delivery)
                && (deliverLocally(delivery)
                || deliverRemotely(delivery));
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
        try {
            culture.get(delivery.getReceiver()).receive(delivery.getMessage());
        } catch (Exception e) {
            logError(e, delivery);
        }
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
        return put(new Address(generator.generate()), zell);
    }

    public Address put(Address address, Zell zell) {
        culture.put(address, zell);
        return address;
    }

    public Zell remove(Address address) {
        return culture.remove(address);
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

    protected void logError(Exception e, Delivery delivery) {
        System.err.println("Caught\n  " + e + "\n  while delivering\n  " + delivery);
        e.printStackTrace();
    }

    private class DishSignalListener implements SignalListener {

        public boolean onDeliver(Delivery delivery) {
            return deliver(delivery);
        }

        public boolean onJoin(Connection connection) {
            return connect(connection) != null;
        }

        public boolean onLeave(Connection connection) {
            disconnect(connection);
            return true;
        }
    }
}
