package project2.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author aria
 *
 * Makes the table of the calendar database and puts a few example events in
 * it the first time the database is used.
 *
 * Derby has no "CREATE TABLE IF NOT EXISTS", so the database is asked first
 * whether the table is already there. That is the same trick that was used
 * for the tutorial databases in the lectures.
 */
public final class Schema {

    public static final String TABLE_NAME = "EVENT";

    private static final int TITLE_LENGTH = 120;
    private static final int LOCATION_LENGTH = 120;

    private static final String CREATE_TABLE =
            "CREATE TABLE EVENT ("
            + "ID INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "EVENT_DATE DATE NOT NULL, "
            + "START_TIME TIME NOT NULL, "
            + "TITLE VARCHAR(" + TITLE_LENGTH + ") NOT NULL, "
            + "LOCATION VARCHAR(" + LOCATION_LENGTH + "))";

    /**
     * Creates the table of the calendar, but only if it is not there yet.
     */
    public static void createIfNotExists(Connection connection) throws SQLException {
        if (tableExists(connection, TABLE_NAME)) {
            return;
        }

        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(CREATE_TABLE);
        } finally {
            statement.close();
        }
    }

    /**
     * Whether the given table is already in the database. Derby keeps the
     * list of its tables in the meta data, so the meta data is asked.
     */
    public static boolean tableExists(Connection connection, String name) throws SQLException {
        ResultSet tables = connection.getMetaData().getTables(null, null, name, null);
        try {
            return tables.next();
        } finally {
            tables.close();
        }
    }

    /**
     * Puts a few example events in the table, but only when the table is
     * empty. If the table already has events then they are the ones the user
     * has saved and they must not be in the way.
     */
    public static void seedIfEmpty(Connection connection) throws SQLException {
        if (countEvents(connection) > 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        insertExample(connection, today, LocalTime.of(9, 30), "Team standup", "Room 4B");
        insertExample(connection, today, LocalTime.of(13, 0), "Lunch with Sam", "Cafe on the corner");
        insertExample(connection, today, LocalTime.of(15, 0), "Code review", "Lab 2");
        insertExample(connection, tomorrow, LocalTime.of(9, 0), "Lecture", "Lecture theatre");
        insertExample(connection, tomorrow, LocalTime.of(18, 30), "Football practice", "The field");
    }

    /**
     * Puts one example event into the table.
     */
    private static void insertExample(Connection connection, LocalDate date, LocalTime time,
            String title, String location) throws SQLException {
        String sql = "INSERT INTO EVENT (EVENT_DATE, START_TIME, TITLE, LOCATION) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            statement.setDate(1, Date.valueOf(date));
            statement.setTime(2, Time.valueOf(time));
            statement.setString(3, title);
            statement.setString(4, location);
            statement.executeUpdate();
        } finally {
            statement.close();
        }
    }

    /**
     * How many events are in the table.
     */
    private static int countEvents(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM EVENT");
            try {
                if (result.next()) {
                    return result.getInt(1);
                }
                return 0;
            } finally {
                result.close();
            }
        } finally {
            statement.close();
        }
    }
}
