package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.StringMessage;

import java.io.IOException;

class CommandLineInterface {

    private final Dish dish;
    private final User user;

    CommandLineInterface(User user, Dish dish) {
        this.user = user;
        this.dish = dish;

        user.listen(new InputListener());
    }

    private class InputListener implements User.InputListener {

        public void hears(String input) {
            if (input.length() == 0) {
                return;
            }

            InputParser parser;
            try {
                parser = new InputParser(input);
            } catch (Exception e) {
                user.tell("Parsing error: " + e.getMessage());
                return;
            }

            try {
                dish.send(parser.getAddress(), parser.getMessage());
            } catch (Exception e) {
                user.tell("Failed to deliver message: " + e);
            }
        }
    }
}
