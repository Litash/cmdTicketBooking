package org.example.cli.client;

import org.example.model.Show;

import java.io.*;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;

public class AdminClient implements Client {

    private final BufferedReader in;
    private final PrintStream out;

    public AdminClient(InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = out;
    }

    public static Show setupShow(String showNumber, int numOfRows, int seatsPerRow, int cancelWindowMins) {
        return null;
    }

    @Override
    public int run() throws IOException {
        out.print("\n\n====== Welcome! You are an Admin. ======\n" +
                "Available commands: \n" +
                "Setup <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n" +
                "View <Show Number>\n" +
                "Exit\n\n" + INPUT_LINE_PREFIX);
        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)){
            String[] cmdArr = cmd.split(" ");
            switch (cmdArr[0].toLowerCase()) {
                case "setup":
                    if (cmdArr.length < 5){
                        return 0;
                    }
                    String showNumber = cmdArr[1];
                    int numOfRows = Integer.parseInt(cmdArr[2]);
                    int seatsPerRow = Integer.parseInt(cmdArr[3]);
                    int cancelWindowMins = Integer.parseInt(cmdArr[4]);
                    Show newShow = AdminClient.setupShow(showNumber, numOfRows, seatsPerRow, cancelWindowMins);
            }
            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }

        return 0;
    }
}
