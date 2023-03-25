package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;


class MainTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @BeforeEach
    public final void resetOutputStreams() {
        out.reset();
        err.reset();
    }
    
    @Test
    void run() {
        String input2 = "exit\n";
        
    }

    @Test
    @DisplayName("should get role from args or input")
    void getRole() throws IOException {
        String outText = "Select your role: \nAdmin\nBuyer\n> ";
        String expectedRole = "something";
        String[] args = new String[]{expectedRole};
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        String role = Main.getRole(args, in, new PrintStream(out));
        assertThat(role).isEqualTo(expectedRole);

        args = new String[0];
        in = new BufferedReader(new StringReader(expectedRole));
        Main.getRole(args, in, new PrintStream(out));
        assertThat(out).hasToString(outText);
    }

    @Test
    void adminFlow() {
    }

    @Test
    void buyerFlow() {
    }
}