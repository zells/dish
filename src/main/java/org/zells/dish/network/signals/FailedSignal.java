package org.zells.dish.network.signals;

import org.zells.dish.network.Signal;

public class FailedSignal implements Signal {

    private String cause;

    public FailedSignal() {
    }

    public FailedSignal(String cause) {
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

    @Override
    public int hashCode() {
        return cause == null ? getClass().hashCode() : cause.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FailedSignal
                && (cause == null && ((FailedSignal) obj).cause == null
                || cause != null && cause.equals(((FailedSignal) obj).cause));
    }
}
