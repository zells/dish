package org.zells.cortex;

import org.zells.cortex.synapses.communicator.CommunicatorSynapse;
import org.zells.cortex.synapses.listener.ListenerSynapse;
import org.zells.cortex.zells.ReceiverZell;
import org.zells.dish.delivery.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

class CortexGui extends JFrame {
    private JDesktopPane desktop;
    private Cortex cortex;
    private Point nextLocation = new Point(0, 0);

    private CortexGui(Cortex cortex) {
        super("Zells Cortex");
        this.cortex = cortex;

        setBounds(200, 100, 1200, 700);

        desktop = new JDesktopPane();
        setContentPane(desktop);
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Zells");
        menu.setMnemonic(KeyEvent.VK_Z);
        menuBar.add(menu);

        addMenuItem(menu, "New Receiver...", KeyEvent.VK_R, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name",
                        "New Receiver...",
                        JOptionPane.PLAIN_MESSAGE);

                if (name == null) {
                    return;
                }

                Address receiver = cortex.dish.add(new ReceiverZell(cortex.dish));
                cortex.book.put(name, receiver);
                addSynapse(new ListenerSynapse(name, receiver, cortex.dish));
            }
        });
        addMenuItem(menu, "New Listener...", KeyEvent.VK_L, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameOrAddress = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name of Address of Zell",
                        "New Listener...",
                        JOptionPane.PLAIN_MESSAGE);

                if (nameOrAddress == null) {
                    return;
                }

                Address target;
                if (cortex.book.has(nameOrAddress)) {
                    target = cortex.book.get(nameOrAddress);
                } else {
                    target = Address.fromString(nameOrAddress);
                }

                addSynapse(new ListenerSynapse(nameOrAddress, target, cortex.dish));
            }
        });
        addMenuItem(menu, "New communicator...", KeyEvent.VK_T, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameOrAddress = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name of Address of Zell",
                        "New Communicator...",
                        JOptionPane.PLAIN_MESSAGE);

                if (nameOrAddress == null) {
                    return;
                }

                Address target = null;
                if (cortex.book.has(nameOrAddress)) {
                    target = cortex.book.get(nameOrAddress);
                } else if (!nameOrAddress.isEmpty()) {
                    target = Address.fromString(nameOrAddress);
                }

                addSynapse(new CommunicatorSynapse(target, cortex.dish, cortex.book));
            }
        });
        addMenuItem(menu, "Quit", KeyEvent.VK_Q, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });

        return menuBar;
    }

    private void addMenuItem(JMenu menu, String caption, int shortCut, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(caption);
        menuItem.setMnemonic(shortCut);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(shortCut, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(action);
        menu.add(menuItem);
    }

    private void addSynapse(Synapse synapse) {
        synapse.setSize(400, 300);
        synapse.setLocation(nextLocation);
        calculateNextPosition();
        synapse.setVisible(true);
        desktop.add(synapse);
        try {
            synapse.setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {
        }
    }

    private void calculateNextPosition() {
        nextLocation = new Point(nextLocation.x + 400, nextLocation.y);
        if (nextLocation.x > getWidth()) {
            nextLocation = new Point(0, nextLocation.y + 300);
        }
        if (nextLocation.y > getHeight()) {
            nextLocation = new Point(40, 40);
        }
    }

    private void quit() {
        cortex.stop();
        System.exit(0);
    }

    static void start(final Cortex cortex) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setLookAndFeel();
                CortexGui frame = new CortexGui(cortex);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);

                frame.addSynapse(new CommunicatorSynapse(cortex.book.get("cortex"), cortex.dish, cortex.book));

                Address receiver = cortex.dish.add(new ReceiverZell(cortex.dish));
                cortex.book.put("me", receiver);
                frame.addSynapse(new ListenerSynapse("me", receiver, cortex.dish));
            }
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
