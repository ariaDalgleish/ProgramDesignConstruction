package assignment1;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * @author aria
 *
 * Reads calendar events from a local text file. All parsing/storage logic
 * lives in AbstractCalendarSource (the parent class) - this class is only
 * responsible for getting lines of text either from disk or, via
 * importFromUrl, from a URL, and handing them off to the parent.
 */
public class LocalFileCalendarSource extends AbstractCalendarSource {

    private final Path filePath;

    public LocalFileCalendarSource(String filePath) {
        this.filePath = Path.of(filePath);

        if (!Files.exists(this.filePath)) {
            configured = false; // no calendar imported yet - not an error
            return;
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath);
            loadLines(lines);
        } catch (IOException e) {
            System.out.println("  (warning: could not read calendar file - " + e.getMessage() + ")");
            configured = false;
        }
    }

    /**
     * Fetches text from the given URL, saves it as this source's local file,
     * then reloads events from it using the same parsing logic as a normal
     * file (inherited from AbstractCalendarSource). Expects the URL to
     * return plain text in the same date,time,title,location format as the
     * local file (not a real .ics feed yet).
     *
     * @param url the calendar URL to import from
     * @return true if the import succeeded and this source is now configured
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
            List<String> lines = Files.readAllLines(filePath);
            loadLines(lines); // inherited from AbstractCalendarSource

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