package org.zells.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUser extends User implements Runnable {

    ConsoleUser() {
        (new Thread(this)).start();
    }

    @Override
    public void tell(String output) {
        System.out.println();
        System.out.println(output);
        System.out.print("> ");
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try {
            while ((input = reader.readLine()) != null) {
                hear(input);
                if (input.length() == 0) {
                    System.out.print("> ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

