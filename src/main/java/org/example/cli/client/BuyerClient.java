package org.example.cli.client;


import org.example.database.DBManager;
import org.example.exception.MyDBException;
import org.example.model.Booking;
import org.example.model.Show;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.cli.CommandLineApp.INPUT_LINE_PREFIX;
import static org.example.cli.client.ClientUtils.checkAvailableCommands;
import static org.example.cli.client.ClientUtils.checkParameters;


public class BuyerClient implements Client{
    private static final List<String> AVAILABLE_COMMANDS = List.of("availability", "book", "cancel", "logout", "exit");
    private final BufferedReader in;
    private final PrintStream out;

    private final DBManager dbManager;

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

            if ("cancel".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 3, out);
                if (paramStatus != 200) return paramStatus;

                returnCode = cancelTicket(cmdArr);
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

    private int cancelTicket(String[] cmdArr) {
        String ticketID = cmdArr[1];
        int phone = Integer.valueOf(cmdArr[2]);
        Booking booking = dbManager.getBookingByShowAndPhone(ticketID);
        Show show = dbManager.getShow(booking.getShowNumber());
        
        Timestamp bookingTime = booking.getBookingTime();
        int cancelWindow = show.getCancellationWindowMins();
        Instant bookingInstant = bookingTime.toInstant();
        Instant windowCloseTime = bookingInstant.plus(cancelWindow, ChronoUnit.MINUTES);
        if (windowCloseTime.isAfter(Instant.now())) {
            dbManager.deleteBooking(booking);
            return 200;
        } else {
            out.println("Sorry, cancellation window for show "+ show.getShowNumber() +" has closed at " + Timestamp.from(windowCloseTime) +
                    ", you cannot cancel your booking.");
        }
        return 400;
    }

    private int bookShowTicket(String[] cmdArr) {
        String showNumber = cmdArr[1];
        Integer phone = Integer.parseInt(cmdArr[2]); //todo: validation
        List<String> seats = Arrays.stream(cmdArr[3].split(",")).map(String::toUpperCase).collect(Collectors.toList()); 
        String ticketID = UUID.randomUUID().toString();
        Timestamp ts = Timestamp.from(Instant.now()); //todo: check
        Booking booking = new Booking(ticketID, phone, showNumber, seats, ts);
        try {
            int bookingResult = dbManager.saveBooking(booking);
            int updateResult = updateShowAvailability(showNumber, seats);
            if (bookingResult == 1 && updateResult == 1) { //todo: handle other possible results
                out.println("A ticket has been booked for show "+ showNumber + ". Ticket number is " + ticketID);
                return 200;
            }
        } catch (MyDBException e) {
            out.println("Cannot book a ticket for the show "+ showNumber);
        }
        return 500;
    }

    private int updateShowAvailability(String showNumber, List<String> bookedSeats) {
        Show show = dbManager.getShow(showNumber);
        show.getAvailableSeats().removeAll(bookedSeats);
        return dbManager.updateShow(show, show.getAvailableSeats());
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
