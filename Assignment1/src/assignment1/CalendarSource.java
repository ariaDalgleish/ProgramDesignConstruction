/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author aria
 *
 * Abstraction for anything that can supply calendar events.
 * The Pet class only ever talks to this interface, never to a concrete
 * implementation directly - this is what lets you add a web/URL-based
 * source later without changing Pet at all (Open/Closed + Dependency
 * Inversion principles).
 */
public interface CalendarSource {
    
    /** Returns the events scheduled for the given date, earliest first. */
    List<CalendarEvent> getEventsFor(LocalDate date);
 
    /** Whether a real calendar has been imported yet. */
    boolean isConfigured();
    
    /**
     * Attempts to import calendar data from a URL. Returns true if the
     * import succeeded and this source is now configured, false otherwise.
     */
    boolean importFromUrl(String url);
}
