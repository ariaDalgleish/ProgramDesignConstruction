package project2.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aria
 *
 * Thrown when an event is not valid. 
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

    public List<String> getProblems() {
        return new ArrayList<String>(problems);
    }

    private static String firstProblem(List<String> problems) {
        if (problems.isEmpty()) {
            return "The event is not valid.";
        }
        return problems.get(0);
    }
}
