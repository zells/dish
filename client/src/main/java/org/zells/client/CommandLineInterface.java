package org.zells.client;

import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.messages.StringMessage;

class CommandLineInterface {

    private final Dish dish;

    CommandLineInterface(User user, Dish dish) {
        this.dish = dish;
        user.listen(new InputListener());
    }

    private class InputListener implements User.InputListener {

        public void hears(String input) {
            if (input.length() == 0) {
                return;
            }

            String[] receiverMessage = input.split(" ");

            try {
                dish.send(Address.fromString(receiverMessage[0]), new StringMessage(receiverMessage[1]));
            } catch (Exception e) {
                System.err.println("Failed");
            }
        }
    }
}
