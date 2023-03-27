package org.example.cli.client;

import org.example.database.DBAccess;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientFactory {

    private final DBAccess dbAccess;

    public ClientFactory(DBAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    public Client getClient(String role, InputStream in, PrintStream out) {
        if ("admin".equalsIgnoreCase(role)) {
            return new AdminClient(dbAccess, in, out);
        } 
        
        if ("buyer".equalsIgnoreCase(role)) {
            return new BuyerClient(dbAccess, in, out);
        } 
        
        return null;
    }
}
