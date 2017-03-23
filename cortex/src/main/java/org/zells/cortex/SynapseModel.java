package org.zells.cortex;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import java.util.HashSet;
import java.util.Set;

abstract public class SynapseModel {

    private final Address target;
    private final Dish dish;
    private Set<Observer> observers = new HashSet<Observer>();

    public SynapseModel(Address target, Dish dish) {
        this.target = target;
        this.dish = dish;

        start();
    }

    protected abstract void start();

    protected void send(Message message) {
        for (Observer o : observers) {
            o.onSent(message);
        }
        dish.send(target, message);
    }

    protected Address add(final Zell zell) {
        return dish.add(new Zell() {
            @Override
            public void receive(Message message) {
                for (Observer o : observers) {
                    o.onReceived(message);
                }
                zell.receive(message);
            }
        });
    }

    void addObserver(Observer observer) {
        observers.add(observer);
    }

    interface Observer {

        void onSent(Message message);

        void onReceived(Message message);
    }
}
