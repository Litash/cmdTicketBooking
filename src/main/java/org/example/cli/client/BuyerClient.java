package org.example.cli.client;


import org.example.database.DBManager;

import java.io.*;
import java.util.List;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;
import static org.example.cli.client.ClientUtils.checkParameters;


public class BuyerClient implements Client{
    private static final List<String> AVAILABLE_COMMANDS = List.of("availability", "book", "cancel", "logout", "exit");
    private BufferedReader in;
    private final PrintStream out;

    public BuyerClient(DBManager dbConnection, InputStream in, PrintStream out) {
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

        int returnCode = 0;
        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)) {
            String[] cmdArr = cmd.split(" ");

            if ("availability".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 5, out);
                if (paramStatus != 200) return paramStatus;
                
                return showAvailability(cmdArr[1]);
            }

            if ("logout".equalsIgnoreCase(cmdArr[0])) {
                return 321;
            }

            if (!AVAILABLE_COMMANDS.contains(cmdArr[0].toLowerCase())) {
                out.println("Command is not supported.");
                returnCode = 405;
            }

            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }

        return returnCode;
    }

    private int showAvailability(String showNumber) {
        return 0;
    }
}
