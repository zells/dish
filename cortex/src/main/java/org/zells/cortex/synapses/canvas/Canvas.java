package org.zells.cortex.synapses.canvas;

import org.zells.cortex.SynapseModel;
import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

abstract public class Canvas extends SynapseModel {

    Canvas(Address target, Dish dish) {
        super(target, dish);
    }

    @Override
    protected void start() {
        Address me = add(new ReceiverZell());
        send(new CompositeMessage(new StringMessage("canvas"))
                .put("add", new AddressMessage(me)));
    }

    private class ReceiverZell implements Zell {
        @Override
        public void receive(Message message) {
            if (message.read(0).asString().equals("draw")) {
                List<Brush> brushes = new ArrayList<Brush>();

                for (int i = 0; i < message.read(1).keys().size(); i++) {
                    Message shape = message.read(1).read(i);

                    if (shape.read(0).asString().equals("line")) {
                        brushes.add(new LineBrush(
                                shape.read("startX").asInteger(),
                                shape.read("startY").asInteger(),
                                shape.read("endX").asInteger(),
                                shape.read("endY").asInteger()
                        ));
                    } else if (shape.read(0).asString().equals("circle")) {
                        brushes.add(new CircleBrush(
                                shape.read("startX").asInteger(),
                                shape.read("startY").asInteger(),
                                shape.read("radius").asInteger()
                        ));
                    }
                }

                draw(brushes);
            }
        }
    }

    abstract void draw(final List<Brush> brushes);

    interface Brush {
        void drawOn(Graphics2D g);
    }

    private class LineBrush implements Brush {
        private final int startX;
        private final int startY;
        private final int endX;
        private final int endY;

        LineBrush(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        public void drawOn(Graphics2D g) {
            g.drawLine(startX / 10, startY / 10, endX / 10, endY / 10);
        }
    }

    private class CircleBrush implements Brush {
        private final int startX;
        private final int startY;
        private final int radius;

        CircleBrush(int startX, int startY, int radius) {
            this.startX = startX;
            this.startY = startY;
            this.radius = radius;
        }

        @Override
        public void drawOn(Graphics2D g) {
            g.drawOval((startX - radius) / 10, (startY - radius) / 10, (radius * 2) / 10, (radius * 2) / 10);
        }
    }
}
