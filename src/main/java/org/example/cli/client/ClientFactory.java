package org.example.cli.client;

import org.example.cli.client.AdminClient;
import org.example.cli.client.BuyerClient;
import org.example.cli.client.Client;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientFactory {
    public Client getClient(String role, InputStream in, PrintStream out) {
        if ("admin".equalsIgnoreCase(role)) {
            return new AdminClient(in, out);
        } 
        
        if ("buyer".equalsIgnoreCase(role)) {
            return new BuyerClient(in, out);
        } 
        
        return null;
    }
}
