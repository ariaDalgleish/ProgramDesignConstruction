package project2.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author aria
 *
 * The tests for the Event class. An event is only a piece of data - a day,
 * a time, a title and a place - so it has no user interface and it can be
 * tested all by itself.
 *
 * The rules that are checked here are the ones that the window relies on.
 * For example the window is allowed to hand over a title with spaces around
 * it, so the Event has to tidy it up before anybody else sees it.
 */
public class EventTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 26);
    private static final LocalTime MORNING = LocalTime.of(9, 0);
    private static final LocalTime AFTERNOON = LocalTime.of(14, 30);

    /**
     * A brand new event has not been saved yet, so it has no id.
     */
    @Test
    public void aNewEventIsNotSaved() {
        Event event = new Event(DAY, MORNING, "Dentist", "Clinic");

        assertFalse("a new event should not be saved", event.isSaved());
        assertEquals("a new event should have no id", Event.UNSAVED_ID, event.getId());
    }

    /**
     * An event that has been given an id has been saved.
     */
    @Test
    public void anEventWithAnIdIsSaved() {
        Event event = new Event(7, DAY, MORNING, "Dentist", "Clinic");

        assertTrue("an event with an id should be saved", event.isSaved());
        assertEquals("the event should have the id it was given", 7, event.getId());
    }

    /**
     * An event must have a day, otherwise there is no way of knowing when it
     * happens.
     */
    @Test
    public void aNullDateIsRefused() {
        try {
            new Event(null, MORNING, "Dentist", "Clinic");
            fail("an event with no date should have been refused");
        } catch (IllegalArgumentException e) {
            assertNotNull("the exception should explain what is wrong", e.getMessage());
        }
    }

    /**
     * An event must have a time, otherwise there is no way of knowing when it
     * happens.
     */
    @Test
    public void aNullTimeIsRefused() {
        try {
            new Event(DAY, null, "Dentist", "Clinic");
            fail("an event with no time should have been refused");
        } catch (IllegalArgumentException e) {
            assertNotNull("the exception should explain what is wrong", e.getMessage());
        }
    }

    /**
     * An event must have a title, so that the user can tell what it is.
     */
    @Test
    public void aNullTitleIsRefused() {
        try {
            new Event(DAY, MORNING, null, "Clinic");
            fail("an event with no title should have been refused");
        } catch (IllegalArgumentException e) {
            assertNotNull("the exception should explain what is wrong", e.getMessage());
        }
    }

    /**
     * A title of nothing but spaces is no better than no title at all.
     */
    @Test
    public void aBlankTitleIsRefused() {
        try {
            new Event(DAY, MORNING, "   ", "Clinic");
            fail("an event with a blank title should have been refused");
        } catch (IllegalArgumentException e) {
            assertNotNull("the exception should explain what is wrong", e.getMessage());
        }
    }

    /**
     * The place is optional, so a missing place simply becomes an empty
     * string instead of causing trouble.
     */
    @Test
    public void aNullLocationBecomesAnEmptyString() {
        Event event = new Event(DAY, MORNING, "Dentist", null);

        assertEquals("a missing place should become an empty string", "", event.getLocation());
    }

    /**
     * The user often types spaces by accident, so the title is tidied up.
     */
    @Test
    public void theTitleIsTrimmed() {
        Event event = new Event(DAY, MORNING, "  Dentist  ", "Clinic");

        assertEquals("the spaces around the title should be removed", "Dentist", event.getTitle());
    }

    /**
     * The place is tidied up in the same way as the title.
     */
    @Test
    public void theLocationIsTrimmed() {
        Event event = new Event(DAY, MORNING, "Dentist", "  Clinic  ");

        assertEquals("the spaces around the place should be removed", "Clinic", event.getLocation());
    }

    /**
     * The day and the time are kept exactly as they were given.
     */
    @Test
    public void theDateAndTheTimeAreKept() {
        Event event = new Event(DAY, AFTERNOON, "Dentist", "Clinic");

        assertEquals("the day should be kept", DAY, event.getDate());
        assertEquals("the time should be kept", AFTERNOON, event.getTime());
    }

    /**
     * withId gives back a new event that has the id. The event that was
     * asked must not be changed, because it belongs to somebody else.
     */
    @Test
    public void withIdReturnsACopyWithTheId() {
        Event original = new Event(DAY, MORNING, "Dentist", "Clinic");

        Event copy = original.withId(7);

        assertNotNull("withId should give back an event", copy);
        assertEquals("the copy should have the new id", 7, copy.getId());
        assertTrue("the copy should be saved", copy.isSaved());
        assertEquals("the original should not have been given the id", Event.UNSAVED_ID, original.getId());
        assertFalse("the original should still be unsaved", original.isSaved());
    }

    /**
     * The copy has the same day, time, title and place as the original.
     */
    @Test
    public void theCopyHasTheSameDetailsAsTheOriginal() {
        Event original = new Event(DAY, AFTERNOON, "Dentist", "Clinic");

        Event copy = original.withId(7);

        assertEquals("the copy should have the same day", DAY, copy.getDate());
        assertEquals("the copy should have the same time", AFTERNOON, copy.getTime());
        assertEquals("the copy should have the same title", "Dentist", copy.getTitle());
        assertEquals("the copy should have the same place", "Clinic", copy.getLocation());
    }

    /**
     * Two events that hold the same details are the same event, so they are
     * equal and they have the same hash code.
     */
    @Test
    public void twoEventsWithTheSameDetailsAreEqual() {
        Event first = new Event(DAY, MORNING, "Dentist", "Clinic");
        Event second = new Event(DAY, MORNING, "Dentist", "Clinic");

        assertTrue("two events with the same details should be equal", first.equals(second));
        assertEquals("equal events should have the same hash code", first.hashCode(), second.hashCode());
    }

    /**
     * Events that do not hold the same details are not equal.
     */
    @Test
    public void twoDifferentEventsAreNotEqual() {
        Event first = new Event(DAY, MORNING, "Dentist", "Clinic");
        Event second = new Event(DAY, MORNING, "Dentist", "Hospital");

        assertFalse("events with different details should not be equal", first.equals(second));
    }

    /**
     * The events of a day are shown earliest first, so the comparison sorts
     * by the day before anything else.
     */
    @Test
    public void eventsAreSortedByDate() {
        List<Event> events = new ArrayList<Event>();
        events.add(new Event(DAY.plusDays(1), MORNING, "Third", ""));
        events.add(new Event(DAY.minusDays(1), MORNING, "First", ""));
        events.add(new Event(DAY, MORNING, "Second", ""));

        Collections.sort(events, Event.BY_DATE_AND_TIME);

        assertEquals("the earliest day should come first", "First", events.get(0).getTitle());
        assertEquals("the next day should come second", "Second", events.get(1).getTitle());
        assertEquals("the last day should come last", "Third", events.get(2).getTitle());
    }

    /**
     * Events on the same day are sorted by the time of the day.
     */
    @Test
    public void eventsOnTheSameDayAreSortedByTime() {
        List<Event> events = new ArrayList<Event>();
        events.add(new Event(DAY, AFTERNOON, "Afternoon", ""));
        events.add(new Event(DAY, MORNING, "Morning", ""));

        Collections.sort(events, Event.BY_DATE_AND_TIME);

        assertEquals("the earlier time should come first", "Morning", events.get(0).getTitle());
        assertEquals("the later time should come last", "Afternoon", events.get(1).getTitle());
    }

    /**
     * Two events that start at the same time are sorted by their titles, so
     * that the order never changes from one day to the next.
     */
    @Test
    public void eventsWithTheSameDateAndTimeAreSortedByTitle() {
        List<Event> events = new ArrayList<Event>();
        events.add(new Event(DAY, MORNING, "Bike ride", ""));
        events.add(new Event(DAY, MORNING, "Dentist", ""));
        events.add(new Event(DAY, MORNING, "Quotes", ""));

        Collections.sort(events, Event.BY_DATE_AND_TIME);

        assertEquals("the first title should come first", "Bike ride", events.get(0).getTitle());
        assertEquals("the second title should come second", "Dentist", events.get(1).getTitle());
        assertEquals("the last title should come last", "Quotes", events.get(2).getTitle());
    }
}
