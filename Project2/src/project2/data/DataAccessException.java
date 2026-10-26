package project2.data;

/**
 * @author aria
 *
 * Thrown when the calendar database cannot be read or written.
 *
 * The data package uses this exception instead of java.sql.SQLException, so
 * the window and the services above never have to know that SQL is what is
 * underneath. The database problems are caught once, here, and turned into
 * one exception that means something: the calendar could not be used.
 *
 * Deliberately a checked exception (extends Exception, not
 * RuntimeException), just like CalendarImportException in Assignment 1, so
 * that any method that can fail because of the database is forced by the
 * compiler to either handle it or declare it. That way "the database is not
 * working" is an explicit, visible part of the method signatures instead of
 * something that could quietly slip through.
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
