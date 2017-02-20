package org.zells.dish.fakes;

import org.zells.dish.delivery.Message;
import org.zells.dish.Zell;

public class FakeZell implements Zell {

    public Message received;

    public void receive(Message message) {
        received = message;
    }
}