package org.example;

import org.example.cli.client.ClientFactory;
import org.example.cli.CommandLineApp;

public class Main {
    public static void main(String[] args) {
        ClientFactory clientFactory = new ClientFactory();
        int result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        while (result == 321) {
            // change role
            result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        }
    }
}