package org.example.cli.client;

import org.example.database.DBAccess;
import org.example.model.Show;

import java.io.*;
import java.util.List;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;

public class AdminClient implements Client {

    private final BufferedReader in;
    private final PrintStream out;
    private final DBAccess dbAccess;

    private static List<String> AVAILABLE_COMMANDS = List.of("setup", "view", "logout", "exit");

    public AdminClient(DBAccess dbAccess, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = out;
        this.dbAccess = dbAccess;
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
        boolean isLoop = true;
        while (!"exit".equalsIgnoreCase(cmd) && isLoop) {
            String[] cmdArr = cmd.split(" ");
            if ("setup".equalsIgnoreCase(cmdArr[0])) {
                int result = setupShow(cmdArr);
                if (result == 1) {
                    out.println("\nA new show has been setup successfully");
                    returnCode = 200;
                } else {
                    returnCode = 400;
                }
            }

            if ("view".equalsIgnoreCase(cmdArr[0])) {
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
        Show show = dbAccess.getShow(showNumber);
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
        if (cmdArr.length < 5) {
            out.println("Missing parameters, please try again.");
            return 0;
        }
        String showNumber = cmdArr[1];
        int numOfRows = Integer.parseInt(cmdArr[2]);
        int seatsPerRow = Integer.parseInt(cmdArr[3]);
        int cancelWindow = Integer.parseInt(cmdArr[4]);
        Show newShow = new Show(showNumber, numOfRows, seatsPerRow, cancelWindow);
        return dbAccess.saveShow(newShow);
    }
}
