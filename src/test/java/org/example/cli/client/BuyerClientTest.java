package org.example.cli.client;

import org.example.database.DBManager;
import org.example.database.DBTestUtil;
import org.example.exception.FileLoadException;
import org.example.exception.MyDBException;
import org.example.model.Booking;
import org.example.model.Show;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;

class BuyerClientTest {
    static DBManager testDB;

    @BeforeAll
    static void setUp() throws FileLoadException, MyDBException {
        testDB = new DBManager(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
        testDB.initDatabase();
    }

    @Test
    @DisplayName("should display availability")
    void availability_happy_case() throws Exception {
        Show testShow = new Show("test1", 3, 3, 5);
        testDB.saveShow(testShow);

        withTextFromSystemIn("availability test1", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(200);
                });
    }

    @Test
    @DisplayName("should handle show not found")
    void availability_sad_case() throws Exception {
        withTextFromSystemIn("availability voidShow", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(404);
                });
    }

    @Test
    @DisplayName("should book a ticket and update show availability")
    void book_happy_case() throws Exception {
        final String testShowNumber = "test2";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);

        withTextFromSystemIn("book test2 12341234 A1,B2,C3", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(200);
                    assertThat(updatedShow.getAvailableSeats()).hasSize(testShow.getAvailableSeats().size() - 3);
                });
    }

    @Test
    @DisplayName("should handle existing phone and show pair")
    void book_sad_case_1() throws Exception {
        final String testShowNumber = "sadBooking1";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);
        

        withTextFromSystemIn("book sadBooking1 12341234 A1,B2,C3", "book sadBooking1 12341234 A3,B2,C1", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(418);
                    assertThat(updatedShow.getAvailableSeats()).hasSize(testShow.getAvailableSeats().size() - 3);
                });
    }

    @Test
    @DisplayName("should handle invalid seats")
    void book_sad_case_2() throws Exception {
        final String testShowNumber = "sadBooking2";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);


        withTextFromSystemIn("book sadBooking2 12341234 A4,B0", "book sadBooking2 12341234 aa, bb", "book sadBooking2 12341234 a0,1b", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(400);
                    assertThat(updatedShow.getAvailableSeats()).hasSameElementsAs(testShow.getAvailableSeats());
                });
    }

    @Test
    @DisplayName("should handle invalid phone number in booking")
    void book_sad_case_3() throws Exception {
        final String testShowNumber = "sadBooking3";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);


        withTextFromSystemIn("book sadBooking3 +6512341234 A4,B0", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(400);
                    assertThat(updatedShow.getAvailableSeats()).hasSameElementsAs(testShow.getAvailableSeats());
                });
    }

    @Test
    @DisplayName("should cancel show within cancellation window")
    void cancel_happy_case() throws Exception {
        final String testShowNumber = "testCancelShow";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);
        Instant oldTime = Instant.now().minus(1, ChronoUnit.MINUTES);
        Booking booking = new Booking("testTicket1", 12341234, testShowNumber, List.of("A1", "B2"), Timestamp.from(oldTime));
        testDB.saveBooking(booking);
        
        withTextFromSystemIn("cancel testTicket1 12341234", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(200);
                    assertThat(updatedShow.getAvailableSeats()).hasSameElementsAs(testShow.getAvailableSeats());
                });
    }

    @Test
    @DisplayName("should handle invalid phone number in cancellation")
    void cancel_sad_case_1() throws Exception {
        final String testShowNumber = "sadCancel1";
        final String testTicket = "testTicket1";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);
        Instant oldTime = Instant.now().minus(1, ChronoUnit.MINUTES);
        Booking booking = new Booking(testTicket, 12341234, testShowNumber, List.of("A1", "B2"), Timestamp.from(oldTime));
        testDB.saveBooking(booking);


        withTextFromSystemIn("cancel testTicket1 +6512341234", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(400);
                    assertThat(updatedShow.getAvailableSeats()).hasSameElementsAs(testShow.getAvailableSeats());
                });
    }

    @Test
    @DisplayName("should reject cancellation when cancellation widow has closed")
    void cancel_sad_case_2() throws Exception {
        final String testShowNumber = "sadCancel2";
        final String testTicket = "testTicket2";
        Show testShow = new Show(testShowNumber, 3, 3, 5);
        testDB.saveShow(testShow);
        Instant oldTime = Instant.now().minus(5, ChronoUnit.MINUTES);
        Booking booking = new Booking(testTicket, 12341234, testShowNumber, List.of("A1", "B2"), Timestamp.from(oldTime));
        testDB.saveBooking(booking);


        withTextFromSystemIn("cancel testTicket2 12341234", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    Show updatedShow = testDB.getShow(testShowNumber);
                    assertThat(returnCode).isEqualTo(400);
                    assertThat(updatedShow.getAvailableSeats()).hasSameElementsAs(testShow.getAvailableSeats());
                });
    }

    @Test
    @DisplayName("should handle missing parameters")
    void run_sad_case_1() throws Exception {
        withTextFromSystemIn("availability", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("should handle unsupported command")
    void run_sad_case_2() throws Exception {

        withTextFromSystemIn("alice wonderland", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(405);
                });
    }

    @Test
    @DisplayName("should return code 321 when command is 'logout'")
    void logout() throws Exception {
        withTextFromSystemIn("logout")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int result = testClient.run();
                    assertThat(result).isEqualTo(321);
                });
    }
}