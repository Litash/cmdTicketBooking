package org.example.cli;

import org.example.cli.client.Client;
import org.example.cli.client.ClientFactory;

import java.io.*;
import java.util.List;

public class CommandLineApp {

    private static final String HELP_TEXT_RESOURCE = "/help.txt";
    private String[] args;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    private final ClientFactory clientFactory;
    private static final String ADMIN = "admin";
    private static final String BUYER = "buyer";
    private static final String EXIT = "exit";
    private static final List<String> ALLOWED_OPTS = List.of(ADMIN, BUYER, EXIT);
    public static final String INPUT_LINE_PREFIX = "> ";

    public CommandLineApp(String[] args, ClientFactory clientFactory, InputStream in, PrintStream out, PrintStream err) {
        this.args = args;
        this.in = in;
        this.out = out;
        this.err = err;
        this.clientFactory = clientFactory;
    }

    public int start() {
        try {
            String role = getRole(args, in, out);
            if ("exit".equalsIgnoreCase(role)) {
                return 0;
            }
            return runAsRole(role, in);
        } catch (Exception e) {  //TODO: For real use, catch only the exactly expected types
            return showErrorAndExit(e.getMessage());
        }
    }

    private int runAsRole(String role, InputStream in) throws IOException {
        Client client = clientFactory.getClient(role, in, out);
        return client.run();
    }

    private static String getRole(String[] args, InputStream in, PrintStream out) throws IOException {
        String role;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(in));
        if (args.length > 0 && ALLOWED_OPTS.contains(args[0])){
            role = args[0];
        }else{
            role = promptRoleSelection(out, inputReader);
            while ( !ALLOWED_OPTS.contains(role)) {
                role = promptRoleSelection(out, inputReader);
            }
        }
        return role;
    }

    private static String promptRoleSelection(PrintStream out, BufferedReader inputReader) throws IOException {
        String role;
        out.print("\nSelect your role: \nAdmin\nBuyer\n----- Enter 'exit' to close this app -----\n" + INPUT_LINE_PREFIX);
        role = inputReader.readLine();
        return role;
    }

    private int showErrorAndExit(String errorMessage) {
        err.println("Ticket Booking Client: " + errorMessage);
        err.println("Refer to help page using '--help'");
        return 1;
    }
}
