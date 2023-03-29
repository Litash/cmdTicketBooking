package org.example.cli.client;

import org.example.database.DBManager;
import org.example.database.DBTestUtil;
import org.example.model.Show;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;

class BuyerClientTest {
    static DBManager testDB;

    @BeforeAll
    static void setUp() {
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
    void book_happy_case() throws Exception {
        Show testShow = new Show("test2", 3, 3, 5);
        testDB.saveShow(testShow);

        withTextFromSystemIn("book test2 12341234 A1,B2,C3", "exit")
                .execute(() -> {
                    BuyerClient testClient = new BuyerClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(200);
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