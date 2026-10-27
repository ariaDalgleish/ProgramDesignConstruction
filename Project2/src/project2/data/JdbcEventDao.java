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
import java.util.ArrayList;
import java.util.List;

import project2.model.Event;

/**
 * @author aria
 *
 * The EventDao that talks to the embedded Derby database. 
 *
 * Every statement is a PreparedStatement, so the values that come from the
 * user are put into the statement as values and cannot change the SQL
 * itself.
 */
public class JdbcEventDao implements EventDao {

    private static final String COLUMNS = "ID, EVENT_DATE, START_TIME, TITLE, LOCATION";

    private final Connection connection;

    /**
     * @param connection the connection to the calendar database
     */
    public JdbcEventDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * The events of the given day, earliest first.
     */
    @Override
    public List<Event> findByDate(LocalDate date) throws DataAccessException {
        String sql = "SELECT " + COLUMNS + " FROM EVENT WHERE EVENT_DATE = ?"
                + " ORDER BY START_TIME, TITLE";
        List<Event> events = new ArrayList<Event>();
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(date));
            result = statement.executeQuery();

            while (result.next()) {
                events.add(readEvent(result));
            }
        } catch (SQLException e) {
            throw new DataAccessException("The events of " + date + " could not be read", e);
        } finally {
            close(result);
            close(statement);
        }

        return events;
    }

    /**
     * Saves a new event and gives back the event with the id that the
     * database made for it.
     */
    @Override
    public Event insert(Event event) throws DataAccessException {
        String sql = "INSERT INTO EVENT (EVENT_DATE, START_TIME, TITLE, LOCATION) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setDate(1, Date.valueOf(event.getDate()));
            statement.setTime(2, Time.valueOf(event.getTime()));
            statement.setString(3, event.getTitle());
            statement.setString(4, event.getLocation());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            try {
                if (keys.next()) {
                    return event.withId(keys.getInt(1));
                }
            } finally {
                close(keys);
            }
        } catch (SQLException e) {
            throw new DataAccessException("The event " + event.getTitle() + " could not be saved", e);
        } finally {
            close(statement);
        }

        throw new DataAccessException("The database did not give the new event an id");
    }

    /**
     * How many events are in the table
     */
    @Override
    public int countAll() throws DataAccessException {
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery("SELECT COUNT(*) FROM EVENT");

            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("The events could not be counted", e);
        } finally {
            close(result);
            close(statement);
        }
    }

    /**
     * Reads one row of the table and makes an event out of it.
     */
    private Event readEvent(ResultSet result) throws SQLException {
        int id = result.getInt("ID");
        LocalDate date = result.getDate("EVENT_DATE").toLocalDate();
        LocalTime time = result.getTime("START_TIME").toLocalTime();
        String title = result.getString("TITLE");
        String location = result.getString("LOCATION");

        return new Event(id, date, time, title, location);
    }

    /**
     * Closes a result set or a statement.
     */
    private void close(AutoCloseable thing) {
        if (thing == null) {
            return;
        }

        try {
            thing.close();
        } catch (Exception e) {
        }
    }
}
