package org.example.cli;

import org.example.cli.client.AdminClient;
import org.example.cli.client.BuyerClient;
import org.example.cli.client.ClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandLineAppTest {
    private static final String ADMIN_ROLE ="admin";
    private static final String BUYER_ROLE ="buyer";
    private static final String WRONG_ROLE ="SomethingElse";

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    
    @Mock
    private ClientFactory mockClientFactory;
    @Mock
    private AdminClient mockAdminClient;
    @Mock
    private BuyerClient mockBuyerClient;

    @BeforeEach
    public final void resetOutputStreams() throws IOException {
        out.reset();
        err.reset();
    }
    @Test
    @DisplayName("should start with admin role")
    void start_happy_case_1() throws Exception {
        String[] args = new String[]{ADMIN_ROLE};
        mockAdminClient();

        CommandLineApp testApp = new CommandLineApp(args, mockClientFactory, System.in, System.out, System.err);
        int result = testApp.start();
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("should start with buyer role")
    void start_happy_case_2() throws Exception {
        String[] args = new String[]{BUYER_ROLE};
        mockBuyerClient();

        CommandLineApp testApp = new CommandLineApp(args, mockClientFactory, System.in, System.out, System.err);
        int result = testApp.start();
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("should prompt for role selection if no args passed until proper role is entered")
    void start_happy_case_3() throws Exception {
        String[] args = new String[]{};
        mockAdminClient();
        String text = tapSystemOutNormalized(() -> {
            withTextFromSystemIn("aaa", ADMIN_ROLE)
                    .execute(() -> {
                        CommandLineApp testApp = new CommandLineApp(args, mockClientFactory, System.in, System.out, System.err);
                        int result = testApp.start();
                        assertThat(result).isZero();
                    });
        });
        assertThat(text).isEqualTo("\nSelect your role: \nAdmin\nBuyer\n> \nSelect your role: \nAdmin\nBuyer\n> ");
    }

    @Test
    @DisplayName("should return code 1 when exception happened")
    public void start_sad_case_1() throws Exception {
        String[] args = new String[]{BUYER_ROLE};
        when(mockClientFactory.getClient(any(), any(), any())).thenThrow(new RuntimeException("error message"));
        String text = tapSystemErrAndOutNormalized(() -> {
            CommandLineApp testApp = new CommandLineApp(args, mockClientFactory, System.in, System.out, System.err);
            int result = testApp.start();
            assertThat(result).isOne();
        });
        assertThat(text).isEqualTo("Ticket Booking Client: error message\nRefer to help page using '--help'\n");
    }

    private void mockAdminClient() throws IOException {
        when(mockAdminClient.run()).thenReturn(0);
        when(mockClientFactory.getClient(eq(ADMIN_ROLE), any(), any())).thenReturn(mockAdminClient);
    }

    private void mockBuyerClient() throws IOException {
        when(mockBuyerClient.run()).thenReturn(0);
        when(mockClientFactory.getClient(eq(BUYER_ROLE), any(), any())).thenReturn(mockBuyerClient);
    }
}