/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author aria
 * 
 * Drives the whole CUI: On-run check, Sleeping, Awake and Active states,
 * matching the design log. The idle timer is measured from the last time
 * enterActive() was called.
 */
public class Pet {

    private static final long IDLE_TIMEOUT_MILLIS = 15_000; // 15s for easy testing

    private final Scanner scanner = new Scanner(System.in);
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
        //askForName();
        onRunCheck();

        boolean running = true;
        while (running) {
            switch (state) {
                case SLEEPING -> running = sleepingState();
                case AWAKE -> running = awakeState();
                case ACTIVE -> { /* handled inline from awakeState() */ }
            }
        }
        System.out.println("Goodbye!");
    }

    /** Prompts once, before the state cycle starts, for the pet's name. */
    private void askForName() {
        System.out.print("Your calendar pet will be ready soon! \nCan you give me a name for your pet? Simply type any name you wish and hit 'Enter' ... ");
        String input = scanner.nextLine().trim();
        if (!input.isBlank()) {
            name = input;
        }
        System.out.println("Interesting choice! " + name + " should be here any moment now...");
    }

    private void onRunCheck() {
        if (!calendarSource.isConfigured()) {
            System.out.println("No calendar found. Please import a calendar URL to proceed.");
            System.out.print("Enter calendar URL (or press Enter to skip for now): ");
            String url = scanner.nextLine();
            if (!url.isBlank()) {
                boolean success = calendarSource.importFromUrl(url);
                if (success) {
                    System.out.println("Calendar imported successfully!");
                } else {
                    System.out.println("Could not import that calendar. Continuing without one for now.");
                }
            }
        }
        state = PetState.SLEEPING;
    }

    /** Returns false if the program should exit. */
    private boolean sleepingState() {
        System.out.println("\n\"zzz...\" " + name + " is asleep! Type ANYthing and hit 'Enter' to awaken.");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("quit")) {
            return false;
        }
        enterActive("wake");
        state = PetState.AWAKE;
        return true;
    }

    /** Returns false if the program should exit. */
    private boolean awakeState() {
        System.out.println("\n\"Mmmrm...\" " + name + " wakes up! \"Oh! What's the date again? Lets see... yes!\"");
        showCalendarFor(currentDate);
        System.out.println("\nType 'next' to view the next day, 'back' to view the previous day, \n'reset' to go back to the current date, or 'quit' to close.");

        while (true) {
            if (idleTimedOut()) {
                System.out.println("\n" + name + " has fallen asleep from inactivity.");
                state = PetState.SLEEPING;
                return true;
            }

            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "next" -> {
                    enterActive("next");
                    currentDate = currentDate.plusDays(1);
                    System.out.println("\"Flip!\" " + name + " flips the calendar page to the next date.");
                    showCalendarFor(currentDate);
                }
                case "back" -> {
                    enterActive("back");
                    currentDate = currentDate.minusDays(1);
                    System.out.println("\"Flip!\" " + name + " flips the calendar page to the previous date.");
                    showCalendarFor(currentDate);
                }
                case "reset" -> {
                    enterActive("reset");
                    currentDate = LocalDate.now();
                    System.out.println("\"Flip!\" " + name + " flips back to today.");
                    showCalendarFor(currentDate);
                }
                case "quit" -> {
                    return false;
                }
                default -> System.out.println("\"Hmmm?\" " + name + " tilts its head in confusion. \n\"I don't think I know that command?\"");
            }
        }
    }

    /**
     * Enters the ACTIVE state to perform a command. Currently the work
     * happens inline in awakeState() for simplicity, but this method is
     * where the idle timer officially resets - matching the design log's
     * requirement that Active is the reset point for the timer.
     */
    private void enterActive(String command) {
        state = PetState.ACTIVE;
        lastInteractionTime = System.currentTimeMillis();
        state = PetState.AWAKE; // returns to Awake once the action completes
    }

    private boolean idleTimedOut() {
        return (System.currentTimeMillis() - lastInteractionTime) > IDLE_TIMEOUT_MILLIS;
    }

    private void showCalendarFor(LocalDate date) {
        System.out.println("\n" + date.format(formatter).toUpperCase());
        List<CalendarEvent> events = calendarSource.getEventsFor(date);
        if (events.isEmpty()) {
            System.out.println("Oooo, there's um... nothing to do today!");
        } else {
            for (CalendarEvent event : events) {
                System.out.println(event);
            }
        }
    }
}