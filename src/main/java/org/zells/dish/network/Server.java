package org.zells.dish.network;

public interface Server {

    void start(SignalListener listener);

    void stop();

    Connection getConnection();
}
