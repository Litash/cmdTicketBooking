package org.example.cli.client;

import org.example.cli.CommandLineApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AdminClientTest {

    private AdminClient testClient;
    
    @BeforeEach
    void setUp() {
        
    }

    @Test
    @DisplayName("should return code 321 when command is 'logout'")
    void logout() throws Exception {
        withTextFromSystemIn("logout")
                .execute(() -> {
                    AdminClient testClient = new AdminClient(System.in, System.out);
                    int result = testClient.run();
                    assertThat(result).isEqualTo(321);
                });
    }
}