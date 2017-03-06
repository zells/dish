package org.zells.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUser extends User implements Runnable {

    private boolean wasMe = false;

    ConsoleUser() {
        (new Thread(this)).start();
    }

    @Override
    public void tell(String output) {
        if (!wasMe) {
            System.out.println();
        }
        System.out.println(output);
        if (!wasMe) {
            System.out.print("<< ");
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try {
            System.out.print("<< ");
            while ((input = reader.readLine()) != null) {
                wasMe = true;
                hear(input);
                System.out.print("<< ");
                wasMe = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

