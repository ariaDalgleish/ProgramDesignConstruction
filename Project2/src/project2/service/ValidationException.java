package project2.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aria
 *
 * Thrown when an event is not valid. It carries every problem that was
 * found, so that the user can be told about all of them at once instead of
 * one at a time.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    // The list of problems is not saved with the exception, because a List
    // cannot be written to a file.
    private final transient List<String> problems;

    public ValidationException(List<String> problems) {
        super(firstProblem(problems));
        this.problems = new ArrayList<String>(problems);
    }

    /** The things that are wrong with the event. */
    public List<String> getProblems() {
        return new ArrayList<String>(problems);
    }

    /** The message of the exception is the first problem. */
    private static String firstProblem(List<String> problems) {
        if (problems.isEmpty()) {
            return "The event is not valid.";
        }
        return problems.get(0);
    }
}
