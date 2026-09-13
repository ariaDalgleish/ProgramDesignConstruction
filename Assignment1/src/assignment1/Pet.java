package assignment1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author aria
 *
 * Drives the whole CUI: On-run check, Sleeping, Awake and Active states,
 * matching the design log. The idle timer is measured from the last time
 * enterActive() was called.
 *
 * This class no longer talks to System.out/Scanner directly - all actual
 * input/output goes through ConsoleView, so this class is purely
 * responsible for the pet's state logic and dialogue text.
 */
public class Pet {

    private static final long IDLE_TIMEOUT_MILLIS = 15_000; // 15s for easy testing
    private static final long POLL_INTERVAL_MILLIS = 500;

    private final ConsoleView view = new ConsoleView();
    private CalendarSource calendarSource;
    private LocalDate currentDate = LocalDate.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH);

    private PetState state = PetState.SLEEPING;
    private long lastInteractionTime = System.currentTimeMillis();
    private String name = "Your pet"; // default until the user names it

    public Pet(CalendarSource calendarSource) {
        this.calendarSource = calendarSource;
    }

    /** Main entry point - call this once from Main. */
    public void run() {
        askForName(); // Name the pet!
        onRunCheck(); // Look for calendar source

        boolean running = true;
        while (running) {
            switch (state) {
                case SLEEPING -> running = sleepingState();
                case AWAKE -> running = awakeState();
                case ACTIVE -> { /* handled inline from awakeState() */ }
            }
        }
        view.println("Goodbye!");
    }

    /** Prompts once, before the state cycle starts, for the pet's name. */
    private void askForName() {
        view.print("Your calendar pet will be ready soon! \nCan you give me a name for your pet? "
                + "\nSimply type any name you wish and hit 'Enter' ... ");
        String input = view.readLine().trim();
        if (!input.isBlank()) {
            name = input;
        }
        view.println("Interesting choice! " + name + " should be here any moment now...");
    }

    /* Calendar Source if txt file in project!
    ** This will be for future, when user needs to import their calendar URL.
    ** This project has no manual configs so skipping for now.
    */
    private void onRunCheck() {
        if (!calendarSource.isConfigured()) {
            view.println("\nNo calendar found. Please import a calendar URL to proceed.");
            view.print("Enter calendar URL (or press Enter to skip for now): ");
            String url = view.readLine();
            if (!url.isBlank()) {
                boolean success = calendarSource.importFromUrl(url);
                if (success) {
                    view.println("Calendar imported successfully!");
                } else {
                    view.println("Could not import that calendar. Continuing without one for now.");
                }
            }
        }
        state = PetState.SLEEPING;
    }

    /** Returns false if the program should exit. */
    private boolean sleepingState() {
        view.println("\n\"zzz...\" " + name + " is asleep! Type ANYthing and hit 'Enter' to awaken."
                + "\n Type 'quit' and hit enter anytime to exit.");
        String input = view.readLine().trim();
        if (input.equalsIgnoreCase("quit")) {
            return false;
        }
        enterActive();
        state = PetState.AWAKE;
        return true;
    }

    /** Returns false if the program should exit. */
    private boolean awakeState() {
        view.println("\n\"Mmmrm...\" " + name + " wakes up! \"Oh! What's the date again? Lets see... yes!\"");
        showCalendarFor(currentDate);
        view.println("\nType 'next' to view the next day, 'back' to view the previous day, "
                + "\n'reset' to go back to the current date, or 'quit' to close.");

        while (true) {
            view.print("> ");
            String input = view.readLineWithTimeout(POLL_INTERVAL_MILLIS, this::idleTimedOut);
            if (input == null) {
                view.println("\n" + name + " has gone back to sleep from inactivity.");
                goToSleep();
                return true;
            }

            switch (input.trim().toLowerCase()) {
                case "next" -> {
                    enterActive();
                    currentDate = currentDate.plusDays(1);
                    view.println("\"Flip!\" " + name + " flips the calendar page to the next date.");
                    showCalendarFor(currentDate);
                }
                case "back" -> {
                    enterActive();
                    currentDate = currentDate.minusDays(1);
                    view.println("\"Flip!\" " + name + " flips the calendar page to the previous date.");
                    showCalendarFor(currentDate);
                }
                case "reset" -> {
                    enterActive();
                    currentDate = LocalDate.now();
                    view.println("\"Flip!\" " + name + " flips back to today.");
                    showCalendarFor(currentDate);
                }
                case "quit" -> {
                    return false;
                }
                default -> view.println("\"Hmmm?\" " + name + " tilts its head in confusion. "
                        + "\n\"I don't think I know that command?\"");
            }
        }
    }

    /**
     * Enters the ACTIVE state to perform a command. Currently the work
     * happens inline in awakeState() for simplicity, but this method is
     * where the idle timer officially resets - matching the design log's
     * requirement that Active is the reset point for the timer.
     */
    private void enterActive() {
        state = PetState.ACTIVE;
        lastInteractionTime = System.currentTimeMillis();
        state = PetState.AWAKE; // returns to Awake once the action completes
    }

    private boolean idleTimedOut() {
        return (System.currentTimeMillis() - lastInteractionTime) > IDLE_TIMEOUT_MILLIS;
    }

    /**
     * Transitions the pet to SLEEPING and resets the displayed date back to
     * today, so waking up later always starts from the current day rather
     * than wherever the user last navigated to.
     */
    private void goToSleep() {
        state = PetState.SLEEPING;
        currentDate = LocalDate.now();
    }

    private void showCalendarFor(LocalDate date) {
        view.println("\n" + date.format(formatter).toUpperCase());
        List<CalendarEvent> events = calendarSource.getEventsFor(date);
        if (events.isEmpty()) {
            view.println("Oooo, there's um... nothing to do today!");
        } else {
            for (CalendarEvent event : events) {
                view.println(event.toString());
            }
        }
    }
}