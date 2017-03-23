package org.zells.cortex.synapses.communicator;

import org.zells.cortex.Synapse;
import org.zells.cortex.zells.AddressBookZell;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CommunicatorSynapse extends Synapse {

    private final Communicator model;

    private JPanel historyBox;
    private JScrollPane historyScrollPane;

    public CommunicatorSynapse(final Address target, Dish dish, AddressBookZell book) {
        super(getTitle(target, book), target);
        model = new Communicator(target, dish, book);

        setLayout(new BorderLayout());
        add(createSplitPane());
    }

    private static String getTitle(Address target, AddressBookZell book) {
        return target == null
                ? "Communicator"
                : ("Communicator: " + (book.contains(target) ? book.nameOf(target) : target));
    }

    private Component createSplitPane() {
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createHistoryPane(), createInputField());

        final int[] height = new int[]{100};

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int diff = getHeight() - height[0];
                height[0] = getHeight();
                splitPane.setDividerLocation(splitPane.getDividerLocation() + diff);
            }
        });

        return splitPane;
    }

    private Component createHistoryPane() {
        historyBox = new JPanel();
        historyBox.setLayout(new BoxLayout(historyBox, BoxLayout.Y_AXIS));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(historyBox, BorderLayout.SOUTH);

        historyScrollPane = new JScrollPane(historyPanel);
        return historyScrollPane;
    }

    private Component createInputField() {
        final JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    processInput(textArea);
                }
            }
        });
        return textArea;
    }

    private void processInput(JTextArea textArea) {
        String input = textArea.getText().trim();
        textArea.setText("");

        try {
            sendMessage(input);
        } catch (Exception err) {
            String message = err.getMessage() != null ? err.getMessage() : "Invalid input";
            textArea.setText(input + " [" + message + "]");
            textArea.select(input.length(), textArea.getText().length());
        }
    }

    private void sendMessage(String input) throws Exception {
        if (input.isEmpty()) {
            return;
        }

        final JTextArea output = new JTextArea();

        model.send(input, new Communicator.Listener() {
            protected void onParsed(String receiver, Message message) {
                output.setText(receiver + " < " + message);
            }

            protected void onSuccess() {
                output.insert("\u2713 ", 0);
            }

            protected void onFailure(Exception e) {
                e.printStackTrace();
                output.insert("\u2718 ", 0);
                output.append(" [" + e.getMessage() + "]");
            }

            protected void onResponse(int sequence, Message message) {
                output.append("\n[" + sequence +"] >> " + message);
                updateScrollPane();
            }
        });

        historyBox.add(output);
        updateScrollPane();
    }

    synchronized private void updateScrollPane() {
        historyScrollPane.validate();
        JScrollBar sb = historyScrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
        JScrollBar hsb = historyScrollPane.getHorizontalScrollBar();
        hsb.setValue(hsb.getMinimum());
    }
}

