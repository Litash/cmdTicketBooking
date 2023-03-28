package org.example.cli.client;

import org.example.database.DBAccess;
import org.example.database.DBTestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.InputStream;
import java.io.PrintStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminClientTest {

    static DBAccess testDB;

    @BeforeAll
    static void setUp() {
        testDB = new DBAccess(DBTestUtil.testJdbcUrl, DBTestUtil.username, DBTestUtil.password);
        testDB.initDatabase();
    }

    @Test
    void shouldSetupShow() throws Exception {
        withTextFromSystemIn("setup test01 3 3 5", "exit")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(testDB, System.in, System.out);
                    int returnCode = testClient.run();
                    assertThat(returnCode).isZero();
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