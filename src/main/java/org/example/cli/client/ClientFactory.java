package org.example.cli.client;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;

public class ClientFactory {

    private Connection dbConnection;

    public ClientFactory(Connection conn) {
        dbConnection = conn;
    }

    public Client getClient(String role, InputStream in, PrintStream out) {
        if ("admin".equalsIgnoreCase(role)) {
            return new AdminClient(dbConnection, in, out);
        } 
        
        if ("buyer".equalsIgnoreCase(role)) {
            return new BuyerClient(dbConnection, in, out);
        } 
        
        return null;
    }
}
