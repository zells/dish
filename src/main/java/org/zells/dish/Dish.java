package org.zells.dish;

import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Delivery;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.ReceiverNotFoundException;
import org.zells.dish.network.Connection;
import org.zells.dish.network.Server;
import org.zells.dish.network.Signal;
import org.zells.dish.network.SignalListener;
import org.zells.dish.network.encoding.EncodingRepository;
import org.zells.dish.network.signals.DeliverySignal;
import org.zells.dish.network.signals.FailedSignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;
import org.zells.dish.util.UuidGenerator;

import java.util.*;

public class Dish implements SignalListener {

    private Server server;
    private UuidGenerator generator;
    private EncodingRepository encodings;

    private Map<Address, Zell> culture = new HashMap<Address, Zell>();
    private List<Peer> peers = new ArrayList<Peer>();
    private Set<Delivery> delivered = new HashSet<Delivery>();

    public Dish(Server server, UuidGenerator generator, EncodingRepository encodings) {
        this.server = server;
        this.generator = generator;
        this.encodings = encodings;

        server.start(this);
    }

    public void send(Address receiver, Message message) {
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

    public Address add(Zell zell) {
        Address address = new Address(generator.generate());
        culture.put(address, zell);
        return address;
    }

    public void join(Connection connection) {
        Peer peer = connect(connection);
        peer.join(server.getConnection());
    }

    private Peer connect(Connection connection) {
        Peer peer = new Peer(connection, encodings);
        peers.add(peer);
        return peer;
    }

    public Signal respond(Signal signal) {
        boolean success = false;
        if (signal instanceof DeliverySignal) {
            success = deliver(((DeliverySignal) signal).getDelivery());
        } else if (signal instanceof JoinSignal) {
            success = connect(((JoinSignal) signal).getConnection()) != null;
        }

        return success ? new OkSignal() : new FailedSignal();
    }
}
