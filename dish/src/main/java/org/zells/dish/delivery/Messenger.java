package org.zells.dish.delivery;

public class Messenger {

    private static final int DEFAULT_TIME_OUT_SEC = 5;

    private boolean running;
    private Failed failureHandler;
    private Delivered successHandler;
    private boolean wasDelivered = false;
    private Exception hasFailedWith;

    public Messenger(final Runnable deliverer) {
        running = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    deliverer.run();
                    delivered();
                } catch (Exception e) {
                    failed(e);
                }
                running = false;
            }
        }.start();
    }

    private void delivered() {
        if (successHandler != null) {
            successHandler.then();
        }
        wasDelivered = true;
    }

    private void failed(Exception e) {
        if (failureHandler != null) {
            failureHandler.then(e);
        }
        hasFailedWith = e;
    }

    public Messenger when(Failed failed) {
        failureHandler = failed;
        if (hasFailedWith != null) {
            failed.then(hasFailedWith);
        }
        return this;
    }

    public Messenger when(Delivered delivered) {
        successHandler = delivered;
        if (wasDelivered) {
            delivered.then();
        }
        return this;
    }

    public Messenger sync(int timeOutSec) {
        long start = System.currentTimeMillis();
        while (running) {
            Thread.yield();
            if (System.currentTimeMillis() - start > timeOutSec * 1000) {
                throw new RuntimeException("Message delivery timed out");
            }
        }
        return this;
    }

    public Messenger sync() {
        return sync(DEFAULT_TIME_OUT_SEC);
    }

    public interface Failed {
        void then(Exception e);
    }

    public interface Delivered {
        void then();
    }
}
