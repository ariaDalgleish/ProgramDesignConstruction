package project2.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import project2.data.DataAccessException;
import project2.model.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author aria
 *
 * The tests for the CalendarService. This class holds the rules of the
 * calendar - what a good event looks like - and it does the talking to the
 * database, so it can be tested without a window and without a real
 * database by using the InMemoryEventDao.
 */
public class CalendarServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 26);
    private static final LocalDate OTHER_DAY = LocalDate.of(2026, 10, 27);
    private static final LocalTime MORNING = LocalTime.of(9, 0);

    // The longest title that the service allows.
    private static final int MAX_TITLE_LENGTH = 120;

    private InMemoryEventDao eventDao;
    private CalendarService service;

    /**
     * Every test gets a service of its own, with a fresh stand-in for the
     * database, so that the tests do not tread on each other's toes.
     */
    @Before
    public void makeANewService() {
        eventDao = new InMemoryEventDao();
        service = new CalendarService(eventDao);
    }

    /**
     * A good event is saved and is given an id.
     */
    @Test
    public void addingAGoodEventSavesIt() throws DataAccessException, ValidationException {
        Event saved = service.addEvent(DAY, MORNING, "Dentist", "Clinic");

        assertNotNull("the service should give back the saved event", saved);
        assertTrue("the saved event should be saved", saved.isSaved());
        assertTrue("the saved event should have an id", saved.getId() != Event.UNSAVED_ID);
        assertEquals("the event should be in the database", 1, eventDao.countAll());
    }

    /**
     * The saved event keeps the details that were asked for.
     */
    @Test
    public void theSavedEventKeepsItsDetails() throws DataAccessException, ValidationException {
        Event saved = service.addEvent(DAY, MORNING, "Dentist", "Clinic");

        assertEquals("the day should be kept", DAY, saved.getDate());
        assertEquals("the time should be kept", MORNING, saved.getTime());
        assertEquals("the title should be kept", "Dentist", saved.getTitle());
        assertEquals("the place should be kept", "Clinic", saved.getLocation());
    }

    /**
     * The listeners are told when the events change, so that the window can
     * show the new events without being asked.
     */
    @Test
    public void addingAnEventTellsTheListenersThatTheEventsChanged() throws DataAccessException, ValidationException {
        RecordingListener listener = new RecordingListener();
        service.addListener(listener);

        service.addEvent(DAY, MORNING, "Dentist", "Clinic");

        assertEquals("the listener should have been told once", 1, listener.numberOfChanges());
    }

    /**
     * The listeners are told every time the events change.
     */
    @Test
    public void theListenersAreToldAboutEveryChange() throws DataAccessException, ValidationException {
        RecordingListener listener = new RecordingListener();
        service.addListener(listener);

        service.addEvent(DAY, MORNING, "Dentist", "Clinic");
        service.addEvent(DAY, MORNING, "Cinema", "Rex");

        assertEquals("the listener should have been told twice", 2, listener.numberOfChanges());
    }

    /**
     * A title of nothing but spaces is not good enough, so the event is
     * refused and the problem says that the title is the trouble.
     */
    @Test
    public void aBlankTitleIsRefused() throws DataAccessException {
        try {
            service.addEvent(DAY, MORNING, "   ", "Clinic");
            fail("an event with a blank title should have been refused");
        } catch (ValidationException e) {
            assertTrue("the problem should mention the title, but it was: " + e.getProblems(),
                    mentionsTitle(e));
        }
        assertEquals("nothing should have been saved", 0, eventDao.countAll());
    }

    /**
     * An event without a title is refused as well.
     */
    @Test
    public void anEventWithNoTitleIsRefused() throws DataAccessException {
        try {
            service.addEvent(DAY, MORNING, null, "Clinic");
            fail("an event with no title should have been refused");
        } catch (ValidationException e) {
            assertTrue("the problem should mention the title, but it was: " + e.getProblems(),
                    mentionsTitle(e));
        }
        assertEquals("nothing should have been saved", 0, eventDao.countAll());
    }

    /**
     * A title that is too long is refused, because it would not fit on the
     * screen.
     */
    @Test
    public void aTitleThatIsTooLongIsRefused() throws DataAccessException {
        String tooLong = makeTitle(MAX_TITLE_LENGTH + 1);

        try {
            service.addEvent(DAY, MORNING, tooLong, "Clinic");
            fail("an event with a very long title should have been refused");
        } catch (ValidationException e) {
            assertTrue("the problem should mention the title, but it was: " + e.getProblems(),
                    mentionsTitle(e));
        }
        assertEquals("nothing should have been saved", 0, eventDao.countAll());
    }

    /**
     * A title of exactly the longest allowed length is still good enough.
     */
    @Test
    public void aTitleOfTheLongestAllowedLengthIsAccepted() throws DataAccessException, ValidationException {
        String justRight = makeTitle(MAX_TITLE_LENGTH);

        Event saved = service.addEvent(DAY, MORNING, justRight, "Clinic");

        assertNotNull("the event should have been saved", saved);
        assertEquals("the event should be in the database", 1, eventDao.countAll());
    }

    /**
     * An event without a day is refused, because there is no way of knowing
     * when it happens.
     */
    @Test
    public void anEventWithNoDateIsRefused() throws DataAccessException {
        try {
            service.addEvent(null, MORNING, "Dentist", "Clinic");
            fail("an event with no date should have been refused");
        } catch (ValidationException e) {
            assertTrue("the problem should mention the date, but it was: " + e.getProblems(),
                    mentionsDate(e));
        }
        assertEquals("nothing should have been saved", 0, eventDao.countAll());
    }

    /**
     * The events of a day are the ones that were put on that day.
     */
    @Test
    public void eventsOnAGivenDayAreReturned() throws DataAccessException, ValidationException {
        service.addEvent(DAY, MORNING, "Dentist", "Clinic");
        service.addEvent(DAY, LocalTime.of(14, 30), "Cinema", "Rex");
        service.addEvent(OTHER_DAY, MORNING, "Dentist", "Clinic");

        List<Event> todaysEvents = service.eventsOn(DAY);

        assertEquals("there should be two events on that day", 2, todaysEvents.size());
        assertEquals("the first event should be the dentist", "Dentist", todaysEvents.get(0).getTitle());
        assertEquals("the second event should be the cinema", "Cinema", todaysEvents.get(1).getTitle());
    }

    /**
     * A day that has nothing on it has no events.
     */
    @Test
    public void eventsOnAnEmptyDayAreNothing() throws DataAccessException, ValidationException {
        service.addEvent(DAY, MORNING, "Dentist", "Clinic");

        List<Event> otherDaysEvents = service.eventsOn(OTHER_DAY);

        assertTrue("there should be no events on that day", otherDaysEvents.isEmpty());
    }

    /**
     * The service can say how many events there are.
     */
    @Test
    public void theEventsCanBeCounted() throws DataAccessException, ValidationException {
        service.addEvent(DAY, MORNING, "Dentist", "Clinic");
        service.addEvent(OTHER_DAY, MORNING, "Cinema", "Rex");

        assertEquals("there should be two events", 2, service.countEvents());
    }

    /**
     * Makes a title of the given length, so that the long title test is
     * easy to read.
     */
    private String makeTitle(int length) {
        StringBuilder title = new StringBuilder();

        for (int i = 0; i < length; i++) {
            title.append('x');
        }

        return title.toString();
    }

    /**
     * Looks through the problems of the exception for one that mentions the
     * title.
     */
    private boolean mentionsTitle(ValidationException e) {
        List<String> problems = e.getProblems();

        for (String problem : problems) {
            if (problem.toLowerCase().contains("title")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Looks through the problems of the exception for one that mentions the
     * date.
     */
    private boolean mentionsDate(ValidationException e) {
        List<String> problems = e.getProblems();

        for (String problem : problems) {
            if (problem.toLowerCase().contains("date")) {
                return true;
            }
        }

        return false;
    }

    /**
     * A listener that simply counts how many times it has been told that
     * the events have changed.
     */
    private static class RecordingListener implements CalendarListener {

        private int changes = 0;

        @Override
        public void eventsChanged() {
            changes = changes + 1;
        }

        /** How many changes the listener has been told about. */
        int numberOfChanges() {
            return changes;
        }
    }
}
