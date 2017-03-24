package org.zells.cortex.synapses;

import org.zells.cortex.Synapse;
import org.zells.cortex.SynapseModel;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardSynapse extends Synapse {

    public KeyboardSynapse(String name, Address target, Dish dish) {
        super("Keyboard: " + name, target);

        setLayout(new BorderLayout());
        final JTextArea input = new JTextArea("Click here and type");
        add(input);

        setModel(new SynapseModel(target, dish) {
            @Override
            protected void start() {
                input.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        e.consume();

                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                            send(new CompositeMessage(
                                    new StringMessage("key"),
                                    new StringMessage("up")
                            ));
                        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            send(new CompositeMessage(
                                    new StringMessage("key"),
                                    new StringMessage("down")
                            ));
                        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            send(new CompositeMessage(
                                    new StringMessage("key"),
                                    new StringMessage("left")
                            ));
                        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            send(new CompositeMessage(
                                    new StringMessage("key"),
                                    new StringMessage("right")
                            ));
                        }
                    }
                });
            }
        });
    }
}
