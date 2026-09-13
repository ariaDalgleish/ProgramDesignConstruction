package assignment1;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.BooleanSupplier;

/**
 * @author aria
 *
 * Owns all console input and output for the program. Pet talks to this
 * class instead of System.out/Scanner directly - this is the Single
 * Responsibility split: Pet decides *what* to say and *when* to ask for
 * input, ConsoleView handles *how* that actually happens on the console.
 *
 * Keeping this separate also means if this project ever grew a GUI version,
 * only this class would need replacing - Pet's state logic wouldn't change
 * at all.
 */
public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public void println(String message) {
        System.out.println(message);
    }

    public void print(String message) {
        System.out.print(message);
    }

    /** Reads one line of input, blocking until the user presses Enter.
     * @return */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Waits for a line of input, but polls a timeout check while waiting
     * instead of blocking forever. Returns the line typed, or null if
     * timedOut returned true before any input arrived.
     *
     * System.in.available() reports how many bytes are currently waiting
     * to be read without blocking - if it's 0, nothing has been typed yet,
     * so we sleep briefly and check the timeout again rather than calling
     * scanner.nextLine() (which would block indefinitely).
     *
     * @param pollMillis how long to sleep between timeout checks
     * @param timedOut   supplies true once the caller considers this a timeout
     * @return 
     */
    public String readLineWithTimeout(long pollMillis, BooleanSupplier timedOut) {
        try {
            while (System.in.available() == 0) {
                if (timedOut.getAsBoolean()) {
                    return null;
                }
                Thread.sleep(pollMillis);
                // Ignore warning
            }
            return scanner.nextLine();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}