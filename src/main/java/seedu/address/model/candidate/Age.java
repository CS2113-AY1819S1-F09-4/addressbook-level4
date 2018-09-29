package seedu.address.model.candidate;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Candidate's age in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAge(String)}
 */

public class Age {
    public static final String AGE_VALIDATION_REGEX = "[\\d]{1,2}";

    public static final String MESSAGE_AGE_CONSTRAINTS =
            "Age has to be a number 0 - 99, and it should not be blank ";

    public final String age;

    public Age(String ageInput) {
        requireNonNull(ageInput);
        checkArgument(isValidAge(ageInput), MESSAGE_AGE_CONSTRAINTS);
        age = ageInput;
    }

    /**
     * Returns true if a given string is a valid age.
     */
    public static boolean isValidAge(String test) {
        return test.matches(AGE_VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Age // instanceof handles nulls
                && age.equals(((Age) other).age)); // state check
    }

    @Override
    public int hashCode() {
        return age.hashCode();
    }
}
