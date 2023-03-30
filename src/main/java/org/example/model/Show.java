package org.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Show {
    private final String showNumber;
    private final int numberOfRows;
    private final int numberOfSeatsPerRow;
    private final int cancellationWindowMins;
    private final List<String> availableSeats;

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
        this.availableSeats = Arrays.stream(availableSeats.split(",")).collect(Collectors.toList());
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

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfSeatsPerRow() {
        return numberOfSeatsPerRow;
    }

    public int getCancellationWindowMins() {
        return cancellationWindowMins;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

}
