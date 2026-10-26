package project2.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import project2.data.EventDao;
import project2.model.Event;

/**
 * @author aria
 *
 * A stand-in for the real database, used by the tests of the
 * CalendarService. Instead of putting the events into Derby it keeps them in
 * a list in the memory of the computer, so that the tests are quick and the
 * real database is left alone.
 *
 * The ids are simply 1, 2, 3 and so on, which is all the tests need.
 *
 * This class is not a test by itself - it is a helper that the tests use.
 */
public class InMemoryEventDao implements EventDao {

    private final List<Event> events = new ArrayList<Event>();
    private int nextId = 1;

    /**
     * The events that are on the given day.
     */
    @Override
    public List<Event> findByDate(LocalDate date) {
        List<Event> found = new ArrayList<Event>();

        for (Event event : events) {
            if (event.getDate().equals(date)) {
                found.add(event);
            }
        }

        return found;
    }

    /**
     * Saves the event and gives it the next id. Nothing can go wrong here,
     * because the events are only kept in a list, so this method never
     * throws a DataAccessException.
     */
    @Override
    public Event insert(Event event) {
        Event saved = event.withId(nextId);
        nextId = nextId + 1;
        events.add(saved);

        return saved;
    }

    /**
     * How many events are being kept.
     */
    @Override
    public int countAll() {
        return events.size();
    }
}
