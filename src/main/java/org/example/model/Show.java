package org.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Show {
    private String showNumber;
    private int numberOfRows;
    private int numberOfSeatsPerRow;
    private int cancellationWindowMins;
    private List<String> availableSeats;

    public Show(String showNumber, int numberOfRows, int numberOfSeatsPerRow, int cancellationWindowMins) {
        this.showNumber = showNumber;
        this.numberOfRows = numberOfRows;
        this.numberOfSeatsPerRow = numberOfSeatsPerRow;
        this.cancellationWindowMins = cancellationWindowMins;
        this.availableSeats = calculateAvailableSeats(numberOfRows, numberOfSeatsPerRow);
    }

    public Show(String showNumber, int numberOfRows, int numberOfSeatsPerRow, int cancellationWindowMins, String availableSeats) {
        this.showNumber = showNumber;
        this.numberOfRows = numberOfRows;
        this.numberOfSeatsPerRow = numberOfSeatsPerRow;
        this.cancellationWindowMins = cancellationWindowMins;
        this.availableSeats = convertSeatsToList(availableSeats);
    }

    private List<String> convertSeatsToList(String availableSeats) {
        return Arrays.stream(availableSeats.split(",")).collect(Collectors.toList());
    }

    public List<String> calculateAvailableSeats(int numberOfRows, int numberOfSeatsPerRow) {
        List<String> seatsList = new ArrayList<>();
        for (int i = 1; i <= numberOfRows; i++) {
            for (int j = 1; j <= numberOfSeatsPerRow; j++) {
                String seat = getCharForNumber(i) + j;
                seatsList.add(seat);
            }
        }
        return seatsList;
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }

    public String getShowNumber() {
        return showNumber;
    }

    public void setShowNumber(String showNumber) {
        this.showNumber = showNumber;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfSeatsPerRow() {
        return numberOfSeatsPerRow;
    }

    public void setNumberOfSeatsPerRow(int numberOfSeatsPerRow) {
        this.numberOfSeatsPerRow = numberOfSeatsPerRow;
    }

    public int getCancellationWindowMins() {
        return cancellationWindowMins;
    }

    public void setCancellationWindowMins(int cancellationWindowMins) {
        this.cancellationWindowMins = cancellationWindowMins;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }
}
