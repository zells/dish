package org.zells.cortex.tests.communicator;

import org.junit.Test;
import org.zells.cortex.tests.BaseTest;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReceiveResponsesTest extends BaseTest {

    @Test
    public void createReceiver() {
        dish.put(target, new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read(0).asAddress(), new StringMessage("Hello World"));
            }
        });
        send(". @+", new Listener() {
            @Override
            protected void onResponse(int sequence, Message message) {
                super.onResponse(sequence, message);
                assert message.equals(new StringMessage("Hello World"));
            }
        });
    }

    @Test
    public void multipleReceivers() {
        dish.put(target, new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read(0).asAddress(), new StringMessage("One")).sync();
                dish.send(message.read(1).asAddress(), new StringMessage("Two")).sync();
            }
        });

        final List<String> responded = new ArrayList<String>();
        send(". @+ @+", new Listener() {
            @Override
            protected void onResponse(int sequence, Message message) {
                super.onResponse(sequence, message);
                responded.add(message.asString());
            }
        });

        assert responded.equals(Arrays.asList("One", "Two"));
    }

    @Test
    public void onlyOnce() {
        final boolean[] failed = new boolean[]{false};
        dish.put(target, new Zell() {
            @Override
            public void receive(Message message) {
                dish.send(message.read(0).asAddress(), new StringMessage("One")).sync();
                dish.send(message.read(0).asAddress(), new StringMessage("Two")).sync()
                        .when(new Messenger.Failed() {
                            @Override
                            public void then(Exception e) {
                                failed[0] = true;
                            }
                        });
            }
        });

        final List<String> responded = new ArrayList<String>();
        send(". @+", new Listener() {
            @Override
            protected void onResponse(int sequence, Message message) {
                super.onResponse(sequence, message);
                responded.add(message.asString());
            }
        });

        assert failed[0];
        assert responded.equals(Collections.singletonList("One"));
    }
}
