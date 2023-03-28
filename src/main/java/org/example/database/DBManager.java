package org.example.database;

import org.apache.commons.io.IOUtils;
import org.example.model.Show;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class DBManager {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    
    private Connection conn;
    private Statement statement;
    private ResultSet resultSet;

    public DBManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void initDatabase() {
        
        try {
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to H2 database.");
            // init db
            FileInputStream fis = new FileInputStream("src/main/resources/schema.sql");
            String sql = IOUtils.toString(fis, "UTF-8");
            statement = conn.createStatement();
            statement.execute(sql);
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.err.println("Cannot read schema.sql");
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Show getShow(String searchNumber) {
        
        try {
            resultSet = statement
                    .executeQuery("select * from SHOW where SHOW_NUMBER= \'" + searchNumber + "\' LIMIT 1;");
            resultSet.first();
            
            String showNum = resultSet.getString("SHOW_NUMBER");
            int rows = resultSet.getInt("NUM_OF_ROWS");
            int seatsPerRow = resultSet.getInt("SEATS_PER_ROW");
            int cancellationWindow = resultSet.getInt("CANCELLATION_WINDOW_MINS");
            String availableSeats = resultSet.getString("AVAILABLE_SEATS");
            
            return new Show(showNum, rows, seatsPerRow, cancellationWindow, availableSeats);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public int saveShow(Show show) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "insert into SHOW values (?, ?, ?, ?, ?)"
            );
            preparedStatement.setString(1, show.getShowNumber());
            preparedStatement.setInt(2, show.getNumberOfRows());
            preparedStatement.setInt(3, show.getNumberOfSeatsPerRow());
            preparedStatement.setInt(4, show.getCancellationWindowMins());
            preparedStatement.setString(5, String.join(",", show.getAvailableSeats()));
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return 0;
    }
    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
