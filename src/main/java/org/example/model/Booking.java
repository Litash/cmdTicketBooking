package org.example.model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Booking {
    private String ticketID;
    private Integer phone;
    private String showNumber;
    private List<String> bookedSeats;
    private Timestamp bookingTime;

    public Booking(String ticketID, Integer phone, String showNumber, List<String> bookedSeats, Timestamp bookingTime) {
        this.ticketID = ticketID;
        this.phone = phone;
        this.showNumber = showNumber;
        this.bookedSeats = bookedSeats;
        this.bookingTime = bookingTime;
    }

    public Booking(String ticketID, Integer phone, String showNumber, String bookedSeats, Timestamp bookingTime) {
        this.ticketID = ticketID;
        this.phone = phone;
        this.showNumber = showNumber;
        this.bookedSeats = Arrays.stream(bookedSeats.split(",")).collect(Collectors.toList());
        this.bookingTime = bookingTime;
    }
    
    public String getTicketID() {
        return ticketID;
    }

    public Integer getPhone() {
        return phone;
    }

    public String getShowNumber() {
        return showNumber;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public Timestamp getBookingTime() {
        return bookingTime;
    }

}
