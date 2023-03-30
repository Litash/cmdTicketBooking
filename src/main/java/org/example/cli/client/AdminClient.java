package org.example.cli.client;

import org.example.database.DBManager;
import org.example.model.Show;

import java.io.*;
import java.util.List;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;
import static org.example.cli.client.ClientUtils.checkAvailableCommands;
import static org.example.cli.client.ClientUtils.checkParameters;

public class AdminClient implements Client {

    private final BufferedReader in;
    private final PrintStream out;
    private final DBManager dbManager;

    private static final List<String> AVAILABLE_COMMANDS = List.of("setup", "view", "logout", "exit");

    public AdminClient(DBManager dbManager, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        this.dbManager = dbManager;
    }

    @Override
    public int run() throws IOException {
        int returnCode = 0;
        printMenu();

        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)) {
            String[] cmdArr = cmd.split(" ");
            if ("setup".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 5, out);
                if (paramStatus != 200) {
                    returnCode = 400;
                } else {
                    returnCode = setupShow(cmdArr);
                } 
            }

            if ("view".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 2, out);
                if (paramStatus != 200) {
                    returnCode = 400;
                } else {
                    returnCode = getShow(cmdArr[1]);
                }
                
            }

            if ("logout".equalsIgnoreCase(cmdArr[0])) {
                return 321;
            }
            returnCode = checkAvailableCommands(returnCode, cmdArr, AVAILABLE_COMMANDS, out);
            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }
        
        return returnCode;
    }

    private int setupShow(String[] cmdArr) {
        String showNumber = cmdArr[1];
        Show newShow;
        try {
            int numOfRows = Integer.parseInt(cmdArr[2]);
            int seatsPerRow = Integer.parseInt(cmdArr[3]);
            if (!isSeatConfigValid(numOfRows, seatsPerRow)) {
                out.println("Invalid seats configuration. Max seats per row is 10 and max rows are 26");
                return 418;
            }
            int cancelWindow = Integer.parseInt(cmdArr[4]);
            newShow = new Show(showNumber, numOfRows, seatsPerRow, cancelWindow);
        } catch (NumberFormatException e) {
            printWrongSetupParamFormatMsg();
            return 400;
        }

        int result = dbManager.saveShow(newShow);

        if (result == 1) {
            out.println("\nA new show has been setup successfully");
            return 200;
        } else {
            out.println("\nA show with same number has already been setup."); 
            return 403;
        }
    }

    private int getShow(String showNumber) {
        Show show = dbManager.getShow(showNumber);
        if (show == null) {
            out.println("Show not found.");
            return 404;
        } else {
            printShowDetails(show);
            return 200;
        }
    }

    private void printMenu() {
        out.print("\n\n====== You are an Admin. ======\n" +
                "Available commands: \n" +
                "Setup <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n" +
                "View <Show Number>\n" +
                "Logout\n" +
                "Exit\n" + INPUT_LINE_PREFIX);
    }

    private void printShowDetails(Show show) {
        out.println("\nShow Number = " + show.getShowNumber()
                + "\nNumber of Rows = " + show.getNumberOfRows()
                + "\nNumber of seats per row = " + show.getNumberOfSeatsPerRow()
                + "\nCancellation window in minutes = " + show.getCancellationWindowMins()
                + "\nAvailable seats = " + show.getAvailableSeats());
    }

    private void printWrongSetupParamFormatMsg() {
        out.println("Parameters has wrong format.");
        out.println("Allowed formats: " +
                "\n<Show Number> - String" +
                "\n<Number of Rows> - Integer" +
                "\n<Number of seats per row> - Integer" +
                "\n<Cancellation window in minutes> - Integer");
    }

    private boolean isSeatConfigValid(int numOfRows, int seatsPerRow) {
        return numOfRows<=26 && seatsPerRow<=10;
    }
}
