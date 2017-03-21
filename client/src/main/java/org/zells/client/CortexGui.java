package org.zells.client;

import org.zells.client.synapses.communicator.CommunicatorSynapse;

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
                addSynapse(new CommunicatorSynapse(cortex.book.get("cortex"), cortex.dish, cortex.book));
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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
