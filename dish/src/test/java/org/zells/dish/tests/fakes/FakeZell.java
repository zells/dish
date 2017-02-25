package org.zells.dish.tests.fakes;

import org.zells.dish.Zell;
import org.zells.dish.delivery.Message;

public class FakeZell implements Zell {

    public Message received;

    public void receive(Message message) {
        received = message;
    }
}