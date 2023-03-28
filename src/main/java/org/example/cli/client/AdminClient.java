package org.example.cli.client;

import org.example.database.DBAccess;
import org.example.model.Show;

import java.io.*;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;

public class AdminClient implements Client {

    private final BufferedReader in;
    private final PrintStream out;
    private final DBAccess dbAccess;

    public AdminClient(DBAccess dbAccess, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = out;
        this.dbAccess = dbAccess;
    }

    @Override
    public int run() throws IOException {
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
                if (result == 1) out.println("\nA new show has been setup successfully");
            }

            if ("view".equalsIgnoreCase(cmdArr[0])) {
                getShow(cmdArr[1]);
            }

            if ("logout".equalsIgnoreCase(cmdArr[0])) {
                return 321;
            }

            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }

        return 0;
    }

    private void getShow(String showNumber) {
        Show show = dbAccess.getShow(showNumber);
        out.println("Show Number = " + show.getShowNumber() 
                + "\nNumber of Rows = " + show.getNumberOfRows() 
                + "\nNumber of seats per row = " + show.getNumberOfSeatsPerRow() 
                + "\nCancellation window in minutes = " + show.getCancellationWindowMins() 
                + "\nAvailable seats = " + show.getAvailableSeats());
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
