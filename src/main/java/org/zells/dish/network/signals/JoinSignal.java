package org.zells.dish.network.signals;

import org.zells.dish.network.Signal;

public class JoinSignal implements Signal {

    private String connectionDescription;

    public JoinSignal(String connectionDescription) {
        this.connectionDescription = connectionDescription;
    }

    public String getConnectionDescription() {
        return connectionDescription;
    }

    @Override
    public int hashCode() {
        return connectionDescription.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JoinSignal
                && connectionDescription.equals(((JoinSignal) obj).connectionDescription);
    }
}
