/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author aria
 *
 * Represents a single scheduled event on a given date.
 * Immutable by design - fields are private and final, no setters.
 * This is the encapsulation example for this class.
 */
public final class CalendarEvent implements Comparable<CalendarEvent>{
    private final LocalDate date;
    private final LocalTime time;
    private final String title;
    private final String location;
    
    public CalendarEvent(LocalDate date, LocalTime time, String title, String location){
        if (date == null || time == null || title == null) {
            throw new IllegalArgumentException("date, time and title must not be null");
        }
        this.date = date;
        this.time = time;
        this.title = title;
        this.location = (location == null) ? "" : location;
    }
    
    public LocalDate getDate() {
        return date;
    }
 
    public LocalTime getTime() {
        return time;
    }
 
    public String getTitle() {
        return title;
    }
 
    public String getLocation() {
        return location;
    }
    
    /** Events on the same day are shown earliest-first.
     * @param other */
    @Override
    public int compareTo(CalendarEvent other) {
        return this.time.compareTo(other.time);
    }
 
    @Override
    public String toString() {
        String line = String.format("%s | %s", time, title);
        if (!location.isBlank()) {
            line += "\n        " + location;
        }
        return line;
    }

}
