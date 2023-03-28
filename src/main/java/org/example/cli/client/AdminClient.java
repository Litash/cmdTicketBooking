package org.example.cli.client;

import org.example.database.DBManager;
import org.example.model.Show;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;
import static org.example.cli.client.ClientUtils.checkParameters;

public class AdminClient implements Client {

    private final BufferedReader in;
    private final PrintStream out;
    private final DBManager dbManager;

    private static List<String> AVAILABLE_COMMANDS = List.of("setup", "view", "logout", "exit");

    public AdminClient(DBManager dbManager, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = out;
        this.dbManager = dbManager;
    }

    @Override
    public int run() throws IOException {
        int returnCode = 0;
        out.print("\n\n====== You are an Admin. ======\n" +
                "Available commands: \n" +
                "Setup <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n" +
                "View <Show Number>\n" +
                "Logout\n" +
                "Exit\n" + INPUT_LINE_PREFIX);
        
        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)) {
            String[] cmdArr = cmd.split(" ");
            if ("setup".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 5, out);
                if (paramStatus != 200) return paramStatus;
                returnCode = setupShow(cmdArr);
            }

            if ("view".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 2, out);
                if (paramStatus != 200) return paramStatus;
                
                returnCode = getShow(cmdArr[1]);
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

    private int getShow(String showNumber) {
        Show show = dbManager.getShow(showNumber);
        if (show == null) {
            out.println("Show not found.");
            return 404;
        } else {
            out.println("Show Number = " + show.getShowNumber()
                    + "\nNumber of Rows = " + show.getNumberOfRows()
                    + "\nNumber of seats per row = " + show.getNumberOfSeatsPerRow()
                    + "\nCancellation window in minutes = " + show.getCancellationWindowMins()
                    + "\nAvailable seats = " + show.getAvailableSeats());
            return 200;
        }
    }

    private int setupShow(String[] cmdArr) {
        //todo: validation
        String showNumber = cmdArr[1];
        int numOfRows = Integer.parseInt(cmdArr[2]);
        int seatsPerRow = Integer.parseInt(cmdArr[3]);
        int cancelWindow = Integer.parseInt(cmdArr[4]);
        Show newShow = new Show(showNumber, numOfRows, seatsPerRow, cancelWindow);
        int result = 0;
        try {
            result = dbManager.saveShow(newShow);
        } catch (SQLException e) {
            out.println("\nA show with same number has already been setup"); //todo: update?
            return 403;
        }

        if (result == 1) {
            out.println("\nA new show has been setup successfully");
            return 200;
        } else {
            return 500;
        }
    }
}
