package org.example;

import org.example.cli.CommandLineApp;
import org.example.cli.client.ClientFactory;
import org.example.database.DBManager;
import org.example.exception.FileLoadException;
import org.example.exception.MyDBException;

public class Main {
    public static void main(String[] args) {
        // db connection
        String jdbcUrl = "jdbc:h2:./showBooking";
        String username = "sa";
        String password = "sa";

        DBManager dbManager = new DBManager(jdbcUrl, username, password);
        try {
            dbManager.initDatabase();
        } catch (MyDBException | FileLoadException e) {
            return;
        }

        // start app
        ClientFactory clientFactory = new ClientFactory(dbManager);
        int result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        while (result == 321) {
            // change role
            result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        }

        dbManager.close();
    }
}