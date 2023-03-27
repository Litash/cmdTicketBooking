package org.example.cli.client;

import org.example.database.DBAccess;

import java.io.*;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;

public class BuyerClient implements Client{
    private BufferedReader in;
    private final PrintStream out;

    public BuyerClient(DBAccess dbConnection, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = new PrintStream(out);
    }

    @Override
    public int run() throws IOException {
        out.print("\n\n====== Welcome! You are a buyer. ======\n" +
                "Available commands: \n" +
                "Availability <Show Number>\n" +
                "Book <Show Number> <Phone#> <Comma separated list of seats>\n" +
                "Cancel <Ticket#> <Phone#>\n" +
                "Exit\n\n" + INPUT_LINE_PREFIX);

        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)){


            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }

        return 0;
    }
}
