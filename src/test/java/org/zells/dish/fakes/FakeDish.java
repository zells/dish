package org.zells.dish.fakes;

import org.zells.dish.Dish;
import org.zells.dish.network.Connection;
import org.zells.dish.network.encoding.EncodingRepository;

import java.util.IdentityHashMap;
import java.util.Map;

public class FakeDish extends Dish {

    public final Connection connection;

    private static int lastId = 0;
    private static Map<Integer, Connection> connections = new IdentityHashMap<Integer, Connection>();

    public FakeDish() {
        super(server(), generator(), encodings());
        connection = connections.get(lastId);
        lastId++;
    }

    private static FakeUuidGenerator generator() {
        return new FakeUuidGenerator();
    }

    private static FakeServer server() {
        FakeServer server = new FakeServer();
        connections.put(lastId, server.getConnection());
        return server;
    }

    private static EncodingRepository encodings() {
        EncodingRepository repository = new EncodingRepository();
        repository.add(new FakeEncoding());
        return repository;
    }
}
