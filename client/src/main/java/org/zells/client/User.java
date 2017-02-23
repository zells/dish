package org.zells.client;

import java.util.HashSet;
import java.util.Set;

public abstract class User {

    private Set<InputListener> listeners = new HashSet<InputListener>();

    public abstract void tell(String output);

    void listen(InputListener listener) {
        listeners.add(listener);
    }

    public void hear(String input) {
        for (InputListener listener : listeners) {
            listener.hears(input);
        }
    }

    interface InputListener {
        void hears(String input);
    }
}
