package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE_RANGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EDUCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SALARY;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.joboffer.JobOffer;

/**
 * Adds a job offer to the JobBook with the relevant fields
 */

public class AddJobDetailsCommand extends Command {

    public static final String COMMAND_WORD = "AddJobDetails";

    public static final String MESSAGE_USAGE = "Enter the following details of the job in the format:\n"
            + PREFIX_COMPANY + "COMPANY "
            + PREFIX_JOB + "JOB_TITLE "
            + PREFIX_GENDER + "GENDER "
            + PREFIX_AGE_RANGE + "AGE_RANGE "
            + PREFIX_EDUCATION + "EDUCATION "
            + PREFIX_SALARY + "SALARY\n"
            + "Example: "
            + PREFIX_COMPANY + "McDonalds "
            + PREFIX_JOB + "cashier "
            + PREFIX_GENDER + "M "
            + PREFIX_AGE_RANGE + "20-30 "
            + PREFIX_EDUCATION + "O levels "
            + PREFIX_SALARY + "1200\n";



    public static final String MESSAGE_SUCCESS = "New added job offer: %1$s";
    public static final String MESSAGE_DUPLICATE_JOB_OFFER = "This job offer already exists in the JobBook";
    private final JobOffer toAdd;


    public AddJobDetailsCommand(JobOffer jobOffer) {
        toAdd = jobOffer;
    };

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasJobOffer(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_JOB_OFFER);
        }
        model.addJobOffer(toAdd);
        model.commitJobBook();
        LogicManager.setLogicState("primary");
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    };
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddJobDetailsCommand // instanceof handles nulls
                && toAdd.equals(((AddJobDetailsCommand) other).toAdd));
    }
}
