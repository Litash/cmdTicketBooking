package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.example.cli.client.ClientFactory;
import org.example.cli.CommandLineApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // db connection
        String jdbcUrl = "jdbc:h2:./test";
        String username = "sa";
        String password = "sa";
        
        Connection conn = null;
        try {
            // connect to db
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to H2 database.");
            
            // init db
            ClassLoader classLoader = Main.class.getClassLoader();
            File file = new File(classLoader.getResource("schema.sql").getFile());
            String sql = FileUtils.readFileToString(file, "UTF-8");

            Statement statement = conn.createStatement();
            statement.execute(sql);
            
            // start app
            ClientFactory clientFactory = new ClientFactory(conn);
            int result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
            while (result == 321) {
                // change role
                result = new CommandLineApp(args, clientFactory, System.in, System.out, System.err).start();
            }
            
            //close db connection
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            System.err.println("Cannot read schema.sql");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}