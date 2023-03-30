package org.example.cli.client;


import org.example.database.DBManager;
import org.example.model.Booking;
import org.example.model.Show;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
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
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintStream(out);
        this.dbManager = dbManager;
    }

    @Override
    public int run() throws IOException {
        printMenu();

        int returnCode = 0;
        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)) {
            String[] cmdArr = cmd.split(" ");

            if ("availability".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 2, out);
                if (paramStatus != 200) {
                    returnCode = 400;
                } else {
                    returnCode = printAvailability(cmdArr[1]);
                }
            }

            if ("book".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 4, out);
                if (paramStatus != 200) {
                    returnCode = 400;
                } else {
                    returnCode = bookShowTicket(cmdArr);
                }

            }

            if ("cancel".equalsIgnoreCase(cmdArr[0])) {
                int paramStatus = checkParameters(cmdArr, 3, out);
                if (paramStatus != 200) {
                    returnCode = 400;
                } else {
                    returnCode = cancelTicket(cmdArr);
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

    private int printAvailability(String showNumber) {
        Show show = dbManager.getShow(showNumber);
        if (show == null) {
            out.println("Cannot find show " + showNumber);
            return 404;
        }
        out.println("Show number: " + show.getShowNumber());
        out.println("Available seats: " + show.getAvailableSeats());
        return 200;
    }

    private int bookShowTicket(String[] cmdArr) {
        String showNumber = cmdArr[1];
        Integer phone = parsePhone(cmdArr[2]);
        if (phone == null) {
            return 400;
        }
        List<String> targetSeats = Arrays.stream(cmdArr[3].split(","))
                .map(String::toUpperCase).map(String::trim)
                .collect(Collectors.toList());

        List<Booking> existingBooking = dbManager.getBookingByShowAndPhone(showNumber, phone);
        if (!existingBooking.isEmpty()) {
            out.println("Phone number " + phone + " has already booked for show " + showNumber);
            return 418;
        }

        Show show = dbManager.getShow(showNumber);
        HashSet<String> availableSeats = new HashSet<>(show.getAvailableSeats());
        if (!availableSeats.containsAll(targetSeats)) {
            out.println("Invalid seats selection. Available seats: " + show.getAvailableSeats());
            return 400;
        }
        
        String ticketID = UUID.randomUUID().toString();
        Timestamp currentTime = Timestamp.from(Instant.now());
        Booking booking = new Booking(ticketID, phone, showNumber, targetSeats, currentTime);
        dbManager.saveBooking(booking);
        updateShowAvailability(showNumber, targetSeats);
        out.println("A ticket has been booked for show "+ showNumber + ". Ticket number is " + ticketID);
        return 200;
    }

    private Integer parsePhone(String phoneParam) {
        Integer phone = null;
        try {
            phone = Integer.parseInt(phoneParam);
        } catch (NumberFormatException e) {
            printWrongBookParamFormatMsg();
        }
        return phone;
    }

    private void printWrongBookParamFormatMsg() {
        out.println("Parameters has wrong format.");
        out.println("Allowed formats: " +
                "\n<Show Number> - String" +
                "\n<Ticket#> - String" +
                "\n<Phone#> - Integer only" +
                "\n<Comma separated list of seats> - Comma separated strings e.g. A1,H5");
    }

    private int cancelTicket(String[] cmdArr) {
        String ticketID = cmdArr[1];
        Integer phone = parsePhone(cmdArr[2]);
        if (phone == null) {
            return 400;
        }
        Booking booking = dbManager.getBookingByTicketId(ticketID);
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

    private int updateShowAvailability(String showNumber, List<String> bookedSeats) {
        Show show = dbManager.getShow(showNumber);
        show.getAvailableSeats().removeAll(bookedSeats);
        return dbManager.updateShow(show, show.getAvailableSeats());
    }

    private void printMenu() {
        out.print("\n\n====== Welcome! You are a buyer. ======\n" +
                "Available commands: \n" +
                "Availability <Show Number>\n" +
                "Book <Show Number> <Phone#> <Comma separated list of seats>\n" +
                "Cancel <Ticket#> <Phone#>\n" +
                "Exit\n\n" + INPUT_LINE_PREFIX);
    }
}
