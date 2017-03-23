package org.zells.cortex;

import org.zells.dish.delivery.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public abstract class Synapse extends JInternalFrame {

    private final Address target;
    private SynapseModel model;
    private Runnable onOpenInspector;
    private JMenu menu;

    public Synapse(final String title, Address target) {
        super(title, true, true, true, true);
        this.target = target;
        setJMenuBar(createMenuBar());
    }

    protected void setModel(SynapseModel model) {
        this.model = model;
        addMenuItem("Inspect messages", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (onOpenInspector != null) {
                    onOpenInspector.run();
                }
            }
        });
    }

    void onOpenInspector(Runnable runnable) {
        onOpenInspector = runnable;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menu = new JMenu("Synapse");
        menu.setMnemonic(KeyEvent.VK_Z);
        menuBar.add(menu);

        addMenuItem("Copy target", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(target.toString()), null);
            }
        });

        return menuBar;
    }

    private void addMenuItem(String caption, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(caption);
        menuItem.addActionListener(action);
        menu.add(menuItem);
    }

    SynapseModel getModel() {
        return model;
    }
}
