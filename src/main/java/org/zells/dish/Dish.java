package org.zells.dish;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Delivery;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.network.*;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.util.UuidGenerator;

import java.util.*;

class Dish {

    private Server server;
    private UuidGenerator generator;
    private EncodingRepository encodings;
    private ConnectionRepository connections;

    private Map<Address, Zell> culture = new HashMap<Address, Zell>();
    private List<Peer> peers = new ArrayList<Peer>();
    private Set<Delivery> delivered = new HashSet<Delivery>();

    Dish(Server server, UuidGenerator generator, EncodingRepository encodings, ConnectionRepository connections) {
        this.server = server;
        this.generator = generator;
        this.encodings = encodings;
        this.connections = connections;

        server.start(new DishSignalListener());
    }

    void send(Address receiver, Message message) {
        if (!deliver(new Delivery(receiver, message, generator.generate()))) {
            throw new ReceiverNotFoundException();
        }
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
        culture.get(delivery.getReceiver()).receive(delivery.getMessage());
        return true;
    }

    private boolean deliverRemotely(Delivery delivery) {
        for (Peer peer : peers) {
            if (peer.deliver(delivery)) {
                return true;
            }
        }
        return false;
    }

    Address add(Zell zell) {
        Address address = new Address(generator.generate());
        culture.put(address, zell);
        return address;
    }

    void join(Connection connection) {
        Peer peer = connect(connection);
        peer.join(server.getConnection());
    }

    private Peer connect(Connection connection) {
        Peer peer = new Peer(connection, encodings);
        peers.add(peer);
        return peer;
    }

    private class DishSignalListener extends SignalListener {

        protected boolean onDeliver(Delivery delivery) {
            return deliver(delivery);
        }

        protected boolean onJoin(String connectionDescription) {
            return connect(connections.getConnectionOf(connectionDescription)) != null;
        }
    }
}
