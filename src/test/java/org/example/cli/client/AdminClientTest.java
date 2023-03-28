package org.example.cli.client;

import org.example.database.DBManager;
import org.example.database.DBTestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;

class AdminClientTest {

    static DBManager testDB;

    @BeforeAll
    static void setUp() {
        testDB = new DBManager(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
        testDB.initDatabase();
    }

    @Test
    @DisplayName("should setup show properly")
    void setup_happy_case() throws Exception {
        withTextFromSystemIn("setup test01 3 3 5", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(200);
                });
    }

    @Test
    @DisplayName("should handle command with missing parameters")
    void setup_sad_case() throws Exception {
        withTextFromSystemIn("setup test01 3 3", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("should be able to view the show after setup")
    void view_happy_case() throws Exception {
        withTextFromSystemIn("setup test01 3 3 5", "view test01", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(200);
                });
    }

    @Test
    @DisplayName("should return 404 when show not found")
    void view_sad_case() throws Exception {
        withTextFromSystemIn("view fakeShow", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(404);
                });
    }

    @Test
    @DisplayName("should handle unsupported command")
    void run_sad_case() throws Exception {

        withTextFromSystemIn("alice wonderland", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(405);
                });
    }

    @Test
    @DisplayName("should return code 321 when command is 'logout'")
    void logout() throws Exception {
        withTextFromSystemIn("logout")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int result = testClient.run();
                    assertThat(result).isEqualTo(321);
                });
    }
    
    
}