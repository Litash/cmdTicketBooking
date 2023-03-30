package org.example.database;

import org.example.exception.FileLoadException;
import org.example.exception.MyDBException;
import org.example.model.Booking;
import org.example.model.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    
    private Connection conn;
    private Statement statement;
    private ResultSet resultSet;

    private PreparedStatement preparedStatement;

    Logger logger = LoggerFactory.getLogger(DBManager.class);
    
    public DBManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void initDatabase() throws MyDBException, FileLoadException {

        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream in = classLoader.getResourceAsStream("schema.sql")){
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connected to H2 database.");
            // init db
            if (in != null) {
                String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                statement = conn.createStatement();
                statement.execute(sql);
                System.out.println("Database initialized.");
            } else {
                logger.error("schema.sql is empty. Cannot initialize database.");
            }
        } catch (SQLException e) {
            logger.error("Cannot create db");
            throw new MyDBException("Cannot create db", e);
        } catch (IOException e) {
            logger.error("Cannot read schema.sql");
            throw new FileLoadException("Cannot read schema.sql", e);
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
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public int saveShow(Show show) {
        try {
            preparedStatement = conn.prepareStatement(
                "insert into SHOW values (?, ?, ?, ?, ?)"
        );
            preparedStatement.setString(1, show.getShowNumber());
            preparedStatement.setInt(2, show.getNumberOfRows());
            preparedStatement.setInt(3, show.getNumberOfSeatsPerRow());
            preparedStatement.setInt(4, show.getCancellationWindowMins());
            preparedStatement.setString(5, String.join(",", show.getAvailableSeats()));
            int result = preparedStatement.executeUpdate();
            conn.commit();
            return result;
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }
        return 0;
    }

    public int updateShow(Show show, List<String> availability) {
        try {
            preparedStatement = conn
                    .prepareStatement("update SHOW set AVAILABLE_SEATS= ? where SHOW_NUMBER= ? ;");
            preparedStatement.setString(1, String.join(",", availability));
            preparedStatement.setString(2, show.getShowNumber());
            int result = preparedStatement.executeUpdate();
            conn.commit();
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return 0;
    }

    public int saveBooking(Booking booking) {
        try {
            preparedStatement = conn.prepareStatement(
                    "insert into BOOKING values (?, ?, ?, ?, ?)"
            );
            preparedStatement.setString(1, booking.getTicketID());
            preparedStatement.setInt(2, booking.getPhone());
            preparedStatement.setString(3, booking.getShowNumber());
            preparedStatement.setString(4, String.join(",", booking.getBookedSeats()));
            preparedStatement.setTimestamp(5, booking.getBookingTime());
            int result = preparedStatement.executeUpdate();
            conn.commit();
            return result;
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
        }
        return 0;
    }
    
    public Booking getBookingByTicketId(String ticketId) {
        try {
            resultSet = statement
                    .executeQuery("select * from BOOKING where TICKET_ID= '" + ticketId + "' LIMIT 1;");
            resultSet.first();
            int phone = resultSet.getInt("PHONE");
            String showNumber = resultSet.getString("SHOW_NO");
            String seats = resultSet.getString("BOOKED_SEATS");
            Timestamp bookingTime = resultSet.getTimestamp("BOOKING_TIME");
            return new Booking(ticketId, phone, showNumber, seats, bookingTime);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public List<Booking> getBookingByShowAndPhone(String showNumber, int phone) {
        List<Booking> result = new ArrayList<>();
        try {
            resultSet = statement
                    .executeQuery("select * from BOOKING where SHOW_NO= '" + showNumber + "' and PHONE= " + phone + ";");
            while (resultSet.next()) {
                String ticketId = resultSet.getString("TICKET_ID");
                String seats = resultSet.getString("BOOKED_SEATS");
                Timestamp bookingTime = resultSet.getTimestamp("BOOKING_TIME");
                result.add(new Booking(ticketId, phone, showNumber, seats, bookingTime));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public int deleteBooking(Booking booking) {
        String ticketID = booking.getTicketID();
        try {
            preparedStatement = conn.prepareStatement("delete from BOOKING where TICKET_ID= ?;");
            preparedStatement.setString(1, ticketID);
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage());
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

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
