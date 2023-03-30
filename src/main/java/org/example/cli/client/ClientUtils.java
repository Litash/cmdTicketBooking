package org.example.cli.client;

import java.io.PrintStream;
import java.util.List;

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

    public static int checkAvailableCommands(int returnCode, String[] cmdArr, List<String> availableCmd, PrintStream out) {
        if (!availableCmd.contains(cmdArr[0].toLowerCase())) {
            out.println("Command is not supported.");
            returnCode = 405;
        }
        return returnCode;
    }
}
