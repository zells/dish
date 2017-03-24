package org.zells.cortex.synapses.canvas;

import org.zells.cortex.Synapse;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class CanvasSynapse extends Synapse {

    public CanvasSynapse(String name, Address target, Dish dish) {
        super("Canvas: " + name, target);

        setLayout(new BorderLayout());

        new Canvas(target, dish) {

            @Override
            public void draw(final List<Brush> brushes) {
                final JPanel canvas = new JPanel() {
                    @Override
                    public void paintComponent(Graphics graphics) {
                        super.paintComponent(graphics);
                        Graphics2D g = (Graphics2D) graphics;

                        g.setPaint(Color.blue);

                        AffineTransform tran = AffineTransform.getTranslateInstance(0, getContentPane().getHeight());
                        AffineTransform flip = AffineTransform.getScaleInstance(1d, -1d);
                        tran.concatenate(flip);
                        g.setTransform(tran);

                        for (Canvas.Brush brush : brushes) {
                            brush.drawOn(g);
                        }

                    }
                };
                canvas.setBackground(Color.white);

                getContentPane().removeAll();
                getContentPane().add(canvas);
                validate();
                repaint();
            }
        };
    }
}
