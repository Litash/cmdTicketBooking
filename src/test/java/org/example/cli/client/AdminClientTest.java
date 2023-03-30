package org.example.cli.client;

import org.example.database.DBManager;
import org.example.database.DBTestUtil;
import org.example.exception.FileLoadException;
import org.example.exception.MyDBException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;

class AdminClientTest {

    static DBManager testDB;

    @BeforeAll
    static void setUp() throws FileLoadException, MyDBException {
        testDB = new DBManager(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
        testDB.initDatabase();
    }

    @Test
    @DisplayName("should setup show properly")
    void setup_happy_case() throws Exception {
        withTextFromSystemIn("setup test00 3 3 5", "exit")
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
    @DisplayName("should handle duplicate show setup")
    void setup_sad_case_1() throws Exception {
        withTextFromSystemIn("setup test02 3 3 5", "setup test02 3 3 5", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(403);
                });
    }

    @Test
    @DisplayName("should handle wrong parameter input type in setup")
    void setup_sad_case_2() throws Exception {
        withTextFromSystemIn("setup test02 a 3 5", "setup test02 3 a 5", "setup test02 3 3 a", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("should reject invalid seats configuration")
    void setup_sad_case_3() throws Exception {
        withTextFromSystemIn("setup test02 11 27 5", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("should be able to view the show after setup")
    void view_happy_case() throws Exception {
        withTextFromSystemIn("setup test03 3 3 5", "view test02", "exit")
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