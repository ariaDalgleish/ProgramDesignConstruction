/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment1;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author aria
 */

public class Pet {
    private static final long IDLE_TIMEOUT_MILLIS = 15_000; // 15s for easy testing

    private final Scanner scanner = new Scanner(System.in);
    private CalendarSource calendarSource;
    private LocalDate currentDate = LocalDate.now();
    private PetState state = PetState.SLEEPING;
    private long lastInteractionTime = System.currentTimeMillis();
    
    public Pet(CalendarSource calendarSource) {
        this.calendarSource = calendarSource;
    }
    
       /** Main entry point - call this once from Main. */
    public void run(){
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
    
     private void onRunCheck() {
        if (!calendarSource.isConfigured()) {
            System.out.println("No calendar found. Please import a calendar URL to proceed.");
            System.out.print("Enter calendar URL (or press Enter to skip for now): ");
            String url = scanner.nextLine();
            // Placeholder - real URL/.ics import would go here.
            if (!url.isBlank()) {
                System.out.println("(Import not implemented yet - continuing with local file only.)");
            }
        }
        state = PetState.SLEEPING;
    }
 
    /** Returns false if the program should exit. */
    private boolean sleepingState() {
        System.out.println("\n\"zzz...\" Pet is sleeping. Type anything to wake it up (or 'quit' to exit):");
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
        System.out.println("\n\"Mmmrm\" Your pet wakes up!");
        showCalendarFor(currentDate);
        System.out.println("\nType 'next', 'back', 'reset', or 'quit'.");
 
        while (true) {
            if (idleTimedOut()) {
                System.out.println("\nYour pet has gone back to sleep from inactivity.");
                state = PetState.SLEEPING;
                return true;
            }
 
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
 
            switch (input) {
                case "next" -> {
                    enterActive("next");
                    currentDate = currentDate.plusDays(1);
                    System.out.println("\"Flip!\" Your pet flips the calendar page forward!");
                    showCalendarFor(currentDate);
                }
                case "back" -> {
                    enterActive("back");
                    currentDate = currentDate.minusDays(1);
                    System.out.println("\"Flip!\" Your pet flips the calendar page back!");
                    showCalendarFor(currentDate);
                }
                case "reset" -> {
                    enterActive("reset");
                    currentDate = LocalDate.now();
                    System.out.println("\"Flip!\" Your pet flips back to today!");
                    showCalendarFor(currentDate);
                }
                case "quit" -> {
                    return false;
                }
                default -> System.out.println("\"Hmmm?\" Your pet tilts its head in confusion.");
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
        System.out.println("\n" + date);
        List<CalendarEvent> events = calendarSource.getEventsFor(date);
        if (events.isEmpty()) {
            System.out.println("Nothing to do today!");
        } else {
            for (CalendarEvent event : events) {
                System.out.println(event);
            }
        }
    }
}
