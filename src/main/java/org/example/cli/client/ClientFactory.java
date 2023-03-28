package org.example.cli.client;

import org.example.database.DBManager;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientFactory {

    private final DBManager dbManager;

    public ClientFactory(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public Client getClient(String role, InputStream in, PrintStream out) {
        if ("admin".equalsIgnoreCase(role)) {
            return new AdminClient(dbManager, in, out);
        } 
        
        if ("buyer".equalsIgnoreCase(role)) {
            return new BuyerClient(dbManager, in, out);
        } 
        
        return null;
    }
}
