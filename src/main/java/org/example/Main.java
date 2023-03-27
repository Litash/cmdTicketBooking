package org.example;

import org.example.cli.CommandLineApp;
import org.example.cli.client.ClientFactory;
import org.example.database.DBAccess;

public class Main {
    public static void main(String[] args) {
        // db connection
        String jdbcUrl = "jdbc:h2:./showBooking";
        String username = "sa";
        String password = "sa";

        DBAccess dbAccess = new DBAccess(jdbcUrl, username, password);
        dbAccess.initDatabase();
        
        // start app
        ClientFactory clientFactory = new ClientFactory(dbAccess);
        int result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        while (result == 321) {
            // change role
            result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
        }

        dbAccess.close();
    }
}