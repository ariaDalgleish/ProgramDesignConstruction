/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aria
 */
public class LocalFileCalendarSource implements CalendarSource {
    
    private final Path filePath;
    private final Map<LocalDate, List<CalendarEvent>> eventsByDate = new HashMap<>();
    private boolean configured = false;

    public LocalFileCalendarSource(String filePath) {
        this.filePath = Path.of(filePath);
        loadFromFile();
    }
    
    private void loadFromFile(){
        if(!Files.exists(filePath)){
            configured = false;
            return;
        }
        
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))){
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if(line.isBlank()) {
                    continue;
                }
                try {
                    parseAndStore(line);
                } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e){
                     // Skip a bad line rather than crashing the whole import,
                    // but tell the user so they can fix their file.
                    System.out.println("  (warning: could not read line " + lineNumber
                            + " of the calendar file - skipping it)");
                }
            }
            configured = true;
        } catch (IOException e) {
            System.out.println(" (warning: could not read calendar file!!" + e.getMessage() + ")");
            configured = false;
        }
        
    }
    
    private void parseAndStore(String line) {
        String[] parts = line.split(",", 4);
        LocalDate date = LocalDate.parse(parts[0].trim());
        LocalTime time = LocalTime.parse(parts[1].trim());
        String title = parts[2].trim();
        String location = parts.length > 3 ? parts[3].trim() : "";
 
        CalendarEvent event = new CalendarEvent(date, time, title, location);
        eventsByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(event);
        Collections.sort(eventsByDate.get(date));
    }
 
    @Override
    public List<CalendarEvent> getEventsFor(LocalDate date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }
 
    @Override
    public boolean isConfigured() {
        return configured;
    }
 
    /**
     * Marks this source as configured after an import (e.g. once a URL has
     * been provided and turned into a local file). Placeholder for now -
     * wire up real URL/.ics parsing here later if you get to it.
     */
    public void markConfigured() {
        this.configured = true;
    }
    
}
