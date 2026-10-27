package project2.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import project2.data.DataAccessException;
import project2.data.EventDao;
import project2.model.Event;

/**
 * @author aria
 *
 * The business rules of the calendar.
 *
 * Checks that an event makes sense before it is saved
 * Tells its listeners when the events change (the Observer pattern)
 */
public class CalendarService {

    private static final int MAX_TITLE_LENGTH = 120;
    private static final int MAX_LOCATION_LENGTH = 120;

    private final EventDao eventDao;
    private final List<CalendarListener> listeners = new ArrayList<CalendarListener>();

    public CalendarService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public List<Event> eventsOn(LocalDate date) throws DataAccessException {
        return eventDao.findByDate(date);
    }

    /** Count events stored in the database. */
    public int countEvents() throws DataAccessException {
        return eventDao.countAll();
    }

    /**
     * Checks the event and saves it.
     *
     * @throws ValidationException if something is wrong with the event
     * @throws DataAccessException if the event cannot be saved
     */
    public Event addEvent(LocalDate date, LocalTime time, String title, String location)
            throws ValidationException, DataAccessException {
        Event event = buildEvent(date, time, title, location);
        Event saved = eventDao.insert(event);
        fireEventsChanged();
        return saved;
    }

    public void addListener(CalendarListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CalendarListener listener) {
        listeners.remove(listener);
    }

    /**
     * Checks that the event is sensible. 
     * Every problem that is found is collected
     */
    private Event buildEvent(LocalDate date, LocalTime time, String title, String location)
            throws ValidationException {
        List<String> problems = new ArrayList<String>();

        if (date == null) {
            problems.add("Please choose a date.");
        }
        if (time == null) {
            problems.add("Please choose a start time.");
        }

        String cleanTitle = (title == null) ? "" : title.trim();
        if (cleanTitle.isEmpty()) {
            problems.add("Please give the event a title.");
        } else if (cleanTitle.length() > MAX_TITLE_LENGTH) {
            problems.add("The title must be " + MAX_TITLE_LENGTH + " characters or fewer.");
        }

        String cleanLocation = (location == null) ? "" : location.trim();
        if (cleanLocation.length() > MAX_LOCATION_LENGTH) {
            problems.add("The location must be " + MAX_LOCATION_LENGTH + " characters or fewer.");
        }

        if (!problems.isEmpty()) {
            throw new ValidationException(problems);
        }

        return new Event(date, time, cleanTitle, cleanLocation);
    }

    /** Tells the listeners that the events have changed. */
    private void fireEventsChanged() {
        List<CalendarListener> copy = new ArrayList<CalendarListener>(listeners);
        for (CalendarListener listener : copy) {
            listener.eventsChanged();
        }
    }
}
