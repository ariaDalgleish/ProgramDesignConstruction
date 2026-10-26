package project2.service;

/**
 * @author aria
 *
 * The Observer interface for the calendar. Anything that wants to know
 * when the events change registers one of these with the service.
 *
 * The controller registers one of these, so that the window is refreshed
 * when an event is added.
 */
public interface CalendarListener {

    /** Called every time the events in the database change. */
    void eventsChanged();
}
