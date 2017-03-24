package org.zells.cortex.tests;

import org.junit.Before;
import org.junit.Test;
import org.zells.cortex.synapses.communicator.Communicator;
import org.zells.cortex.zells.TurtleZell;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.Arrays;

public class MoveTurtle extends BaseTest {

    @Before
    @Override
    public void SetUp() {
        super.SetUp();
        target = dish.add(new TurtleZell(dish));
        communicator = new Communicator(target, dish, book);

        dish.put(Address.fromString("ca"), new Zell() {
            @Override
            public void receive(Message message) {
                received.add(message);
            }
        });
    }

    @Test
    public void drawYourself() {
        send("canvas add:@0xca");
        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(500))
                        .put("centerY", new IntegerMessage(500))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(540))
                        .put("endX", new IntegerMessage(500))
                        .put("endY", new IntegerMessage(550))
        ));
    }

    @Test
    public void goForward() {
        send("canvas add:@0xca");
        received.clear();
        send("go forward:100");
        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(500))
                        .put("endY", new IntegerMessage(600)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(500))
                        .put("centerY", new IntegerMessage(600))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(640))
                        .put("endX", new IntegerMessage(500))
                        .put("endY", new IntegerMessage(650))
        ));
    }

    @Test
    public void goBackwards() {
        send("canvas add:@0xca");
        received.clear();
        send("go backwards:100");
        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(500))
                        .put("endY", new IntegerMessage(400)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(500))
                        .put("centerY", new IntegerMessage(400))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(440))
                        .put("endX", new IntegerMessage(500))
                        .put("endY", new IntegerMessage(450))
        ));
    }

    @Test
    public void turnLeft() {
        send("canvas add:@0xca");
        received.clear();
        send("turn left:90");
        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(500))
                        .put("centerY", new IntegerMessage(500))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(460))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(450))
                        .put("endY", new IntegerMessage(500))
        ));
    }

    @Test
    public void turnRight() {
        send("canvas add:@0xca");
        received.clear();
        send("turn right:90");
        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(500))
                        .put("centerY", new IntegerMessage(500))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(540))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(550))
                        .put("endY", new IntegerMessage(500))
        ));
    }

    @Test
    public void turnAndGo() {
        send("canvas add:@0xca");
        send("turn right:90");
        received.clear();
        send("go forward:100");

        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(600))
                        .put("endY", new IntegerMessage(500)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(600))
                        .put("centerY", new IntegerMessage(500))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(640))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(650))
                        .put("endY", new IntegerMessage(500))
        ));
    }

    @Test
    public void keyControl() {
        send("canvas add:@0xca");
        send("key right");
        send("key right");
        send("key right");
        send("key up");
        send("key left");
        send("key left");
        send("key left");
        send("key left");
        send("key left");
        received.clear();
        send("key down");

        assert received.equals(Arrays.asList(
                new CompositeMessage(new StringMessage("clear")),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(500))
                        .put("startY", new IntegerMessage(500))
                        .put("endX", new IntegerMessage(550))
                        .put("endY", new IntegerMessage(586)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(550))
                        .put("startY", new IntegerMessage(586))
                        .put("endX", new IntegerMessage(584))
                        .put("endY", new IntegerMessage(493)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("circle"))
                        .put("centerX", new IntegerMessage(584))
                        .put("centerY", new IntegerMessage(493))
                        .put("radius", new IntegerMessage(50)),
                new CompositeMessage(new StringMessage("draw"), new StringMessage("line"))
                        .put("startX", new IntegerMessage(571))
                        .put("startY", new IntegerMessage(530))
                        .put("endX", new IntegerMessage(567))
                        .put("endY", new IntegerMessage(539))));
    }
}
