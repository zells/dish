package org.zells.cortex;

import org.zells.cortex.synapses.KeyboardSynapse;
import org.zells.cortex.synapses.canvas.CanvasSynapse;
import org.zells.cortex.synapses.communicator.CommunicatorSynapse;
import org.zells.cortex.synapses.keyvalue.KeyValueEditorSynapse;
import org.zells.cortex.synapses.listener.ListenerSynapse;
import org.zells.cortex.zells.ReceiverZell;
import org.zells.cortex.zells.TurtleZell;
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

        JMenu cortexMenu = new JMenu("Cortex");
        cortexMenu.setMnemonic(KeyEvent.VK_C);
        menuBar.add(cortexMenu);
        addMenuItem(cortexMenu, "Quit", KeyEvent.VK_Q, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });

        JMenu synapsesMenu = new JMenu("Synapses");
        synapsesMenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(synapsesMenu);
        addMenuItem(synapsesMenu, "Communicator...", KeyEvent.VK_T, new ActionListener() {
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
        addMenuItem(synapsesMenu, "Listener...", KeyEvent.VK_L, new ActionListener() {
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
        addMenuItem(synapsesMenu, "KeyValueEditor...", 0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameOrAddress = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name of Address of Zell",
                        "New KeyValueEditor...",
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

                addSynapse(new KeyValueEditorSynapse(nameOrAddress, target, cortex.dish));
            }
        });
        addMenuItem(synapsesMenu, "Canvas...", 0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameOrAddress = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name of Address of Zell",
                        "New Canvas...",
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

                addSynapse(new CanvasSynapse(nameOrAddress, target, cortex.dish));
            }
        });
        addMenuItem(synapsesMenu, "Keyboard...", 0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nameOrAddress = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name of Address of Zell",
                        "New Keyboard...",
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

                addSynapse(new KeyboardSynapse(nameOrAddress, target, cortex.dish));
            }
        });


        JMenu zellsMenu = new JMenu("Zells");
        zellsMenu.setMnemonic(KeyEvent.VK_Z);
        menuBar.add(zellsMenu);
        addMenuItem(zellsMenu, "Receiver...", KeyEvent.VK_R, new ActionListener() {
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
            }
        });
        addMenuItem(zellsMenu, "Turtle...", KeyEvent.VK_R, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        CortexGui.this,
                        "Name",
                        "New Turtle...",
                        JOptionPane.PLAIN_MESSAGE);

                if (name == null) {
                    return;
                }

                cortex.book.put(name, cortex.dish.add(new TurtleZell(cortex.dish)));
            }
        });

        return menuBar;
    }

    private void addMenuItem(JMenu menu, String caption, int shortCut, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(caption);
        menu.add(menuItem);
        menuItem.addActionListener(action);

        if (shortCut != 0) {
            menuItem.setMnemonic(shortCut);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortCut, ActionEvent.CTRL_MASK));
        }
    }

    private void addSynapse(final Synapse synapse) {
        addInternalFrame(synapse);

        synapse.onOpenInspector(new Runnable() {
            @Override
            public void run() {
                JInternalFrame inspector = new MessageInspector(synapse);
                addInternalFrame(inspector);
            }
        });
    }

    private void addInternalFrame(JInternalFrame frame) {
        frame.setSize(400, 300);
        frame.setLocation(nextLocation);
        calculateNextPosition(frame);
        frame.setVisible(true);
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {
        }
    }

    private void calculateNextPosition(JInternalFrame frame) {
        nextLocation = new Point(nextLocation.x + frame.getWidth(), nextLocation.y);
        if (nextLocation.x + frame.getWidth() > getWidth()) {
            nextLocation = new Point(0, nextLocation.y + frame.getHeight());
        }
        if (nextLocation.y + frame.getHeight() > getHeight()) {
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

                frame.addSynapse(new KeyValueEditorSynapse("book", cortex.book.get("book"), cortex.dish));
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
