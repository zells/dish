package org.zells.dish.delivery;

public class Messenger {

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

    public interface Failed {
        void then(Exception e);
    }

    public interface Delivered {
        void then();
    }
}
