package org.example.cli.client;


import org.example.database.DBManager;
import org.example.model.Booking;
import org.example.model.Show;

import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;
import static org.example.cli.client.ClientUtils.checkParameters;


public class BuyerClient implements Client{
    private static final List<String> AVAILABLE_COMMANDS = List.of("availability", "book", "cancel", "logout", "exit");
    private BufferedReader in;
    private final PrintStream out;

    private DBManager dbManager;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BuyerClient(DBManager dbManager, InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));;
        this.out = new PrintStream(out);
        this.dbManager = dbManager;
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
                int paramStatus = checkParameters(cmdArr, 2, out);
                if (paramStatus != 200) return paramStatus;
                
                returnCode = showAvailability(cmdArr[1]);
            }

            if ("book".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 4, out);
                if (paramStatus != 200) return paramStatus;

                returnCode = bookShowTicket(cmdArr);
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

    private int bookShowTicket(String[] cmdArr) {
        String showNumber = cmdArr[1];
        Integer phone = Integer.parseInt(cmdArr[2]); //todo: validation
        List<String> seats = Arrays.stream(cmdArr[3].split(",")).collect(Collectors.toList());//todo:validation
        String ticketID = UUID.randomUUID().toString();
        Timestamp ts = Timestamp.from(Instant.now());
        Booking booking = new Booking(ticketID, phone, showNumber, seats, ts);
        try {
            int result = dbManager.saveBooking(booking);
            if (result == 1) {
                out.println("A ticket has been booked for show "+ showNumber + ". Ticket number is " + ticketID);
                //todo: update show availability
                
                return 200;
            }
        } catch (SQLException e) {
            out.println("Cannot book a ticket for the show "+ showNumber);
        }
        return 500;
    }

    private int showAvailability(String showNumber) {
        Show show = dbManager.getShow(showNumber);
        if (show != null) {
            out.println("Show number: " + show.getShowNumber());
            out.println("Available seats: " + show.getAvailableSeats());
            return 200;
        }
        return 404;
    }
}
