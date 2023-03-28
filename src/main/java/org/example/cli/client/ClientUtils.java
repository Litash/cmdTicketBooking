package org.example.cli.client;

import java.io.PrintStream;

class ClientUtils {

    private ClientUtils() {
        throw new IllegalStateException("Utility class");
    }
    public static int checkParameters(String[] cmdArr, int numOfParams, PrintStream out) {
        if (cmdArr.length < numOfParams) {
            out.println("Missing parameters, please try again.");
            return 400;
        }
        return 200;
    }
}
