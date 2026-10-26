package project2.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author aria
 *
 * Represents a single scheduled event on a given date. It is the same idea
 * as the CalendarEvent class in Assignment 1, with a database id added so
 * that the program can find the event again later.
 *
 * Immutable by design - the fields are private and final and there are no
 * setters, so an event that the window is showing cannot change underneath
 * it. This is the encapsulation example for this class.
 *
 * The events are sorted with a separate comparator (BY_DATE_AND_TIME)
 * instead of by implementing Comparable. Two events can have the same date
 * and the same time, and a compareTo based on the sort key would call them
 * equal while equals correctly calls them different. Keeping the two apart
 * avoids that problem.
 */
public final class Event {

    /** The id used for an event that has not been saved to the database yet. */
    public static final int UNSAVED_ID = 0;

    /**
     * Sorts events by date, then by start time, then by title.
     * It is written out by hand as an anonymous class so that it is easy
     * to read.
     */
    public static final Comparator<Event> BY_DATE_AND_TIME = new Comparator<Event>() {
        @Override
        public int compare(Event first, Event second) {
            int result = first.getDate().compareTo(second.getDate());
            if (result != 0) {
                return result;
            }
            result = first.getTime().compareTo(second.getTime());
            if (result != 0) {
                return result;
            }
            return first.getTitle().compareTo(second.getTitle());
        }
    };

    private final int id;
    private final LocalDate date;
    private final LocalTime time;
    private final String title;
    private final String location;

    /**
     * Makes a new event that has not been saved to the database yet.
     *
     * @param date     the day the event happens on
     * @param time     the time the event starts
     * @param title    a short description of the event
     * @param location where the event happens, may be empty
     */
    public Event(LocalDate date, LocalTime time, String title, String location) {
        this(UNSAVED_ID, date, time, title, location);
    }

    /**
     * Makes an event that already has a database id.
     *
     * @param id       the id the database gave the event, or UNSAVED_ID
     * @param date     the day the event happens on
     * @param time     the time the event starts
     * @param title    a short description of the event
     * @param location where the event happens, may be empty
     */
    public Event(int id, LocalDate date, LocalTime time, String title, String location) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (time == null) {
            throw new IllegalArgumentException("time must not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        this.id = id;
        this.date = date;
        this.time = time;
        this.title = title.trim();
        this.location = (location == null) ? "" : location.trim();
    }

    /** @return the database id, or UNSAVED_ID if the event is not saved yet */
    public int getId() {
        return id;
    }

    /** @return the day the event happens on */
    public LocalDate getDate() {
        return date;
    }

    /** @return the time the event starts */
    public LocalTime getTime() {
        return time;
    }

    /** @return the title of the event */
    public String getTitle() {
        return title;
    }

    /** @return where the event happens */
    public String getLocation() {
        return location;
    }

    /** @return true if the database has given this event an id */
    public boolean isSaved() {
        return id != UNSAVED_ID;
    }

    /**
     * Makes a copy of this event with the given database id.
     * This is used when the database gives a new event its id.
     *
     * @param id the id the database gave the event
     * @return the same event, but now saved
     */
    public Event withId(int id) {
        return new Event(id, date, time, title, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Event)) {
            return false;
        }
        Event other = (Event) obj;
        return id == other.id
                && date.equals(other.date)
                && time.equals(other.time)
                && title.equals(other.title)
                && location.equals(other.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, title, location);
    }

    /**
     * The event as it is written under the date - the same format as the
     * console version in Assignment 1.
     */
    @Override
    public String toString() {
        String line = String.format("%s | %s", time, title);
        if (!location.isBlank()) {
            line += "\n        " + location;
        }
        return line;
    }
}
