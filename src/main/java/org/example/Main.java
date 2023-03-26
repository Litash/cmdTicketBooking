package org.example;

import org.example.cli.client.ClientFactory;
import org.example.cli.CommandLineApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // db connection
        String jdbcUrl = "jdbc:h2:mem:testdb";
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");

            // start app
            ClientFactory clientFactory = new ClientFactory();
            int result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
            while (result == 321) {
                // change role
                result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
            }
            
            
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
}