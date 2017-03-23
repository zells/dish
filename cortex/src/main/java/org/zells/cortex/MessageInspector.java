package org.zells.cortex;

import org.zells.dish.delivery.Message;

import javax.swing.*;
import java.awt.*;

class MessageInspector extends JInternalFrame {

    private final JScrollPane scrollPane;

    MessageInspector(Synapse synapse) {
        super("Messages of " + synapse.getTitle(), true, true, true, true);

        final JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(list, BorderLayout.SOUTH);

        scrollPane = new JScrollPane(panel);
        add(scrollPane);

        synapse.getModel().addObserver(new SynapseModel.Observer() {
            @Override
            public void onSent(Message message) {
                list.add(new JTextArea("> " + message.toString()));
                scrollToBottom();
            }

            @Override
            public void onReceived(Message message) {
                list.add(new JTextArea("< " + message.toString()));
                scrollToBottom();
            }
        });
    }

    synchronized private void scrollToBottom() {
        validate();
        JScrollBar sb = scrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
        JScrollBar hsb = scrollPane.getHorizontalScrollBar();
        hsb.setValue(hsb.getMinimum());
    }
}
