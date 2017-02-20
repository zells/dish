package org.zells.dish.network.signals;

import org.zells.dish.network.Connection;
import org.zells.dish.network.Signal;

public class JoinSignal implements Signal {

    private Connection connection;

    public JoinSignal(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
