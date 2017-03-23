package org.zells.cortex.synapses.listener;

import org.zells.cortex.Synapse;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;

import javax.swing.*;
import java.awt.*;

public class ListenerSynapse extends Synapse {

    private final Listener model;
    private final JPanel list;
    private final JScrollPane scrollPane;

    public ListenerSynapse(String name, Address target, Dish dish) {
        super("Listener: " + name, target);

        list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(list, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(panel);
        add(scrollPane);

        model = new Listener(target, dish) {
            @Override
            protected void onReceive(ReceivedMessage message) {
                refreshMessages();
            }
        };
    }

    synchronized private void refreshMessages() {
        list.removeAll();
        for (Listener.ReceivedMessage message : model.getReceivedMessages()) {
            list.add(new JTextArea("[" + message.getTimeAsIsoString() + "] " + message.getMessage().toString()));
        }
        validate();
        scrollToBottom();
    }

    synchronized private void scrollToBottom() {
        JScrollBar sb = scrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
        JScrollBar hsb = scrollPane.getHorizontalScrollBar();
        hsb.setValue(hsb.getMinimum());
    }
}
