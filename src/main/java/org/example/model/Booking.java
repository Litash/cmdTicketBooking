package org.example.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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

    public Booking(Integer phone, String showNumber, List<String> bookedSeats, Timestamp bookingTime) {
        this.ticketID = UUID.randomUUID().toString();
        this.phone = phone;
        this.showNumber = showNumber;
        this.bookedSeats = bookedSeats;
        this.bookingTime = bookingTime;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getShowNumber() {
        return showNumber;
    }

    public void setShowNumber(String showNumber) {
        this.showNumber = showNumber;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public Timestamp getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Timestamp bookingTime) {
        this.bookingTime = bookingTime;
    }
}
