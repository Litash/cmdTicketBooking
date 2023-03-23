package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {

    public static final String INPUT_LINE_PREFIX = "> ";
    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = new PrintStream(System.out);
        PrintStream err = new PrintStream(System.err);

        try {
            new Main().run(args, in, out);
        } catch (Exception e) {  //For real use, catch only the exactly expected types
            err.println(e);
        }
    }

    public void run(String[] args, BufferedReader in, PrintStream out) throws IOException {
        String role;
        if (args.length == 0){
            out.print("Select your role: \nAdmin\nBuyer\n" + INPUT_LINE_PREFIX);
            role = in.readLine();
        }else{
            role = args[0];
        }
        
        if ("admin".equalsIgnoreCase(role)){
            adminFlow(in, out);
        } else if ("buyer".equalsIgnoreCase(role)) {
            buyerFlow(in, out);
        }else {
            out.println("No such role... Exit");
        }
    }

    private static void adminFlow(BufferedReader in, PrintStream out) throws IOException {
        out.print("\n\n====== Welcome! You are an Admin. ======\n" +
                "Available commands: \n" +
                "Setup <Show Number> <Number of Rows> <Number of seats per row> <Cancellation window in minutes>\n" +
                "View <Show Number>\n" +
                "Exit\n\n" + INPUT_LINE_PREFIX);
        String cmd = in.readLine();
        while (!"exit".equalsIgnoreCase(cmd)){
            
            
            out.print(INPUT_LINE_PREFIX);
            cmd = in.readLine();
        }
        
    }

    private static void buyerFlow(BufferedReader in, PrintStream out) {
        System.out.println("Welcome! You are a buyer.");
    }
}