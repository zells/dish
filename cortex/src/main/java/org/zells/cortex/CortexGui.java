package org.zells.cortex;

import org.zells.cortex.synapses.communicator.CommunicatorSynapse;
import org.zells.dish.delivery.Address;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

class CortexGui extends JFrame {
    private JDesktopPane desktop;
    private Cortex cortex;

    private CortexGui(Cortex cortex) {
        super("Zells Cortex");
        this.cortex = cortex;

        setBounds(200, 100, 800, 500);

        desktop = new JDesktopPane();
        setContentPane(desktop);
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Zells");
        menu.setMnemonic(KeyEvent.VK_Z);
        menuBar.add(menu);

        addMenuItem(menu, "New communicator...", KeyEvent.VK_N, new ActionListener() {
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
                    target = CortexGui.this.cortex.book.get(nameOrAddress);
                } else if (!nameOrAddress.isEmpty()) {
                    target = Address.fromString(nameOrAddress);
                }

                addSynapse(new CommunicatorSynapse(target, CortexGui.this.cortex.dish, CortexGui.this.cortex.book));
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
        synapse.setVisible(true);
        desktop.add(synapse);
        try {
            synapse.setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {
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
