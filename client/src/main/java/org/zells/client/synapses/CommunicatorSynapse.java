package org.zells.client.synapses;

import org.zells.client.Cortex;
import org.zells.client.Synapse;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommunicatorSynapse extends Synapse {

    private final Cortex cortex;
    private final Address target;
    private JPanel historyBox;
    private JScrollPane historyScrollPane;

    public CommunicatorSynapse(final Cortex cortex, final Address target) {
        super("Communicator: " + (cortex.book.contains(target) ? cortex.book.nameOf(target) : " *"));
        this.cortex = cortex;
        this.target = target;

        setLayout(new BorderLayout());
        add(createSplitPane());
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

                    String input = textArea.getText().trim();
                    textArea.setText("");

                    if (input.isEmpty()) {
                        return;
                    }

                    try {
                        sendMessage(input);
                    } catch (Exception err) {
                        textArea.setText(input + " [" + err.getMessage() + "]");
                        textArea.select(input.length(), textArea.getText().length());
                    }
                }
            }
        });
        return textArea;
    }

    private void sendMessage(String input) throws Exception {
        JTextArea output = new JTextArea();

        Map<String, Address> aliases = prepareAliases(output);

        InputParser parser = new InputParser(input, new ArrayList<Message>(), aliases);
        Address receiver = resolveAddress(aliases, parser.getReceiver());

        output.setText(parser.getReceiver() + " < " + parser.getMessage());

        waitFor(cortex.dish.send(receiver, parser.getMessage()), output);

        historyBox.add(output);
        updateScrollPane();
    }

    private void waitFor(Messenger messenger, final JTextArea output) {
        messenger.when(new Messenger.Delivered() {
            @Override
            public void then() {
                output.insert("\u2713 ", 0);
            }
        });

        messenger.when(new Messenger.Failed() {
            @Override
            public void then(Exception e) {
                e.printStackTrace();
                output.insert("\u2718 ", 0);
                output.append(" [" + e.getMessage() + "]");
            }
        });
    }

    private Map<String, Address> prepareAliases(final JTextArea output) {
        Map<String, Address> addresses = new HashMap<String, Address>(cortex.book.getAddresses()) {
            public Address get(Object key) {
                if (key.equals("+")) {
                    ReceiverZell receiver = new ReceiverZell(output);
                    Address receiverAddress = cortex.dish.add(receiver);
                    receiver.setAddress(receiverAddress);
                    return receiverAddress;
                }
                return super.get(key);
            }
        };
        addresses.put(".", target);

        return addresses;
    }

    synchronized private void updateScrollPane() {
        historyScrollPane.validate();
        JScrollBar sb = historyScrollPane.getVerticalScrollBar();
        sb.setValue(sb.getMaximum());
        JScrollBar hsb = historyScrollPane.getHorizontalScrollBar();
        hsb.setValue(hsb.getMinimum());
    }

    private Address resolveAddress(Map<String, Address> aliases, String receiver) {
        if (receiver.startsWith("0x")) {
            return Address.fromString(receiver);
        } else if (aliases.containsKey(receiver)) {
            return aliases.get(receiver);
        } else {
            return Address.fromString(receiver);
        }
    }

    private class ReceiverZell implements Zell {
        private JTextArea output;
        private Address address;

        ReceiverZell(JTextArea output) {
            this.output = output;
        }

        @Override
        public void receive(Message message) {
            cortex.dish.remove(address);
            output.append("\n >> " + message);
            updateScrollPane();
        }

        void setAddress(Address address) {
            this.address = address;
        }
    }
}

