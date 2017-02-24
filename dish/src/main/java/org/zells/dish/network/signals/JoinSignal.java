package org.zells.dish.network.signals;

import org.zells.dish.network.Signal;

public class JoinSignal implements Signal {

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JoinSignal;
    }
}
