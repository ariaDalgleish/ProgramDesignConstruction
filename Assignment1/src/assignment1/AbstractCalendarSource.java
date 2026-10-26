package assignment1;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aria
 *
 * Shared logic for any CalendarSource that reads events as lines of text
 * in the format: date,time,title,location
 *
 * Subclasses are only responsible for getting the raw lines from wherever
 * they come from (a local file, a URL, etc) and calling loadLines(). This
 * class then handles parsing, storage and lookup identically for all of
 * them. LocalFileCalendarSource extends this class - that extends
 * relationship is the inheritance example for this part of the project.
 */
public abstract class AbstractCalendarSource implements CalendarSource {

    protected final Map<LocalDate, List<CalendarEvent>> eventsByDate = new HashMap<>();
    protected boolean configured = false;

    /**
     * Parses each line and stores the resulting events. A line that fails
     * to parse throws CalendarImportException, which is caught here so one
     * bad line doesn't stop the rest of the file from loading - but it is
     * still reported to the user rather than silently skipped.
     */
    protected void loadLines(List<String> lines) {
        // The events of the last load are thrown away first, so that loading
        // the calendar again does not add every event a second time.
        eventsByDate.clear();

        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (line.isBlank()) {
                continue;
            }
            try {
                parseAndStore(line);
            } catch (CalendarImportException e) {
                System.out.println("  (warning: line " + lineNumber + " - " + e.getMessage() + ")");
            }
        }
        configured = true;
    }

    /**
     * @param line one line of raw calendar text
     * @throws CalendarImportException if the line cannot be parsed into a
     *                                  valid CalendarEvent
     */
    private void parseAndStore(String line) throws CalendarImportException {
        try {
            String[] parts = line.split(",", 4);
            LocalDate date = LocalDate.parse(parts[0].trim());
            LocalTime time = LocalTime.parse(parts[1].trim());
            String title = parts[2].trim();
            String location = parts.length > 3 ? parts[3].trim() : "";

            CalendarEvent event = new CalendarEvent(date, time, title, location);
            eventsByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(event);
            Collections.sort(eventsByDate.get(date));

        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            throw new CalendarImportException("could not read line \"" + line + "\"", e);
        }
    }

    @Override
    public List<CalendarEvent> getEventsFor(LocalDate date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }

    @Override
    public boolean isConfigured() {
        return configured;
    }
}