package org.zells.client.zells;

import org.zells.client.CommandLineInterface;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Message;

class ListenerZell implements Zell {

    private CommandLineInterface cli;

    ListenerZell(CommandLineInterface cli) {
        this.cli = cli;
    }

    @Override
    public void receive(Message message) {
        cli.receive(message);
    }
}
