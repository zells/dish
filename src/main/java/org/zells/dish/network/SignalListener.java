package org.zells.dish.network;

import org.zells.dish.delivery.Delivery;
import org.zells.dish.network.signals.DeliverSignal;
import org.zells.dish.network.signals.FailedSignal;
import org.zells.dish.network.signals.JoinSignal;
import org.zells.dish.network.signals.OkSignal;

public abstract class SignalListener {

    public Signal respondTo(Signal signal) {
        if (signal instanceof DeliverSignal) {
            return isOk(onDeliver(((DeliverSignal) signal).getDelivery()));
        } else if (signal instanceof JoinSignal) {
            return isOk(onJoin(((JoinSignal) signal).getConnectionDescription()));
        }

        throw new RuntimeException("unknown signal: " + signal.getClass());
    }

    private Signal isOk(boolean ok) {
         return ok ? new OkSignal() : new FailedSignal();
    }

    protected abstract boolean onDeliver(Delivery delivery);

    protected abstract boolean onJoin(String connectionDescription);
}
