package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShowTest {

    @Test
    void calculateAvailableSeats() {
        List<String> expectedAvaSeats = List.of(
                "A1", "A2", "A3",
                "B1", "B2", "B3",
                "C1", "C2", "C3"
        );
        
        Show show = new Show("Show001", 3, 3, 10);
        List<String> avaSeats = show.getAvailableSeats();

        assertThat(avaSeats).hasSameElementsAs(expectedAvaSeats);
    }
}