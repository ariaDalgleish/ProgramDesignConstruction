package assignment1;

/**
 * @author aria
 *
 * Thrown when a calendar source cannot parse or import a line of event
 * data. Deliberately a checked exception (extends Exception, not
 * RuntimeException) so any method that can encounter a bad line is forced
 * by the compiler to either handle it or declare it - the goal is to make
 * "a line of calendar data might be malformed" an explicit, visible part
 * of the method signatures involved, rather than something that could
 * silently slip through.
 */
public class CalendarImportException extends Exception {

    public CalendarImportException(String message) {
        super(message);
    }

    public CalendarImportException(String message, Throwable cause) {
        super(message, cause);
    }
}