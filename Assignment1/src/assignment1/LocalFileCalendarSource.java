/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
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
 * Reads calendar events from a plain text file, one event per line, in the
 * format:
 *
 *   date,time,title,location
 *   2026-09-06,10:00,Church,1 Valley Rd
 *
 * Events are grouped internally in a Map<LocalDate, List<CalendarEvent>>
 * which is kept private - callers only ever get data back through
 * getEventsFor(), never the raw map. That is the encapsulation point here.
 */
public class LocalFileCalendarSource implements CalendarSource {

    private final Path filePath;
    private final Map<LocalDate, List<CalendarEvent>> eventsByDate = new HashMap<>();
    private boolean configured = false;

    public LocalFileCalendarSource(String filePath) {
        this.filePath = Path.of(filePath);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            // No calendar imported yet - not an error, just not configured.
            configured = false;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    parseAndStore(line);
                } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
                    // Skip a bad line rather than crashing the whole import,
                    // but tell the user so they can fix their file.
                    System.out.println("  (warning: could not read line " + lineNumber
                            + " of the calendar file - skipping it)");
                }
            }
            configured = true;
        } catch (IOException e) {
            System.out.println("  (warning: could not read calendar file - " + e.getMessage() + ")");
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
     * Fetches text from the given URL, saves it as this source's local file,
     * then reloads events from it using the same parsing logic as a normal
     * file. Expects the URL to return plain text in the same
     * date,time,title,location format as the local file (not a real .ics
     * feed yet - that needs different parsing in parseAndStore()).
     */
    @Override
    public boolean importFromUrl(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("  (warning: calendar URL returned status " + response.statusCode() + ")");
                return false;
            }

            Files.writeString(filePath, response.body());
            loadFromFile(); // reuse the exact same parsing logic as a local file
            return configured;

        } catch (IOException | InterruptedException e) {
            System.out.println("  (warning: could not import from that URL - " + e.getMessage() + ")");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("  (warning: that doesn't look like a valid URL - " + e.getMessage() + ")");
            return false;
        }
    }
}