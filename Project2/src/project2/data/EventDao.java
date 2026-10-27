package project2.data;

import java.time.LocalDate;
import java.util.List;

import project2.model.Event;

/**
 * @author aria
 *
 * The place where the events are kept (the DAO pattern).
 */
public interface EventDao {

    /**
     * The events of the given day, earliest first.
     *
     * @param date the day to look at
     * @return the events of that day, never null
     * @throws DataAccessException if the events cannot be read
     */
    List<Event> findByDate(LocalDate date) throws DataAccessException;

    /**
     * Saves a new event.
     *
     * @param event the event to save
     * @return the same event, but now with the id that the database gave it
     * @throws DataAccessException if the event cannot be saved
     */
    Event insert(Event event) throws DataAccessException;

    /**
     * How many events are being kept.
     *
     * @return the number of events
     * @throws DataAccessException if the events cannot be counted
     */
    int countAll() throws DataAccessException;
}
