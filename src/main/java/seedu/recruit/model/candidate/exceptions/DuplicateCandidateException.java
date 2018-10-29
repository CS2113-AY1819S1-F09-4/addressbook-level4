package seedu.recruit.model.candidate.exceptions;

/**
 * Signals that the operation will result in duplicate Persons (Persons are considered duplicates if they have the same
 * identity).
 */
public class DuplicateCandidateException extends RuntimeException {
    public DuplicateCandidateException() {
        super("Operation would result in duplicate persons");
    }
}
