package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.model.Model.PREDICATE_SHOW_ALL_COMPANIES;
import static seedu.recruit.model.Model.PREDICATE_SHOW_ALL_JOBOFFERS;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.events.logic.ChangeLogicStateEvent;
import seedu.recruit.commons.events.ui.ShowCompanyBookRequestEvent;
import seedu.recruit.logic.CommandHistory;

import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;

/**
 * First stage of deleting a candidate from a job offer's list of shortlisted candidates.
 * Deletes a candidate identified using its displayed index
 * from the list of shortlisted candidates of a selected job offer.
 */
public class DeleteShortlistedCandidateInitializationCommand extends Command {

    public static final String COMMAND_WORD = "deleteShortlist";

    public static final String COMMAND_LOGIC_STATE = "primary";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the candidate identified by the index number used in the shortlisted candidate list.\n";

    public static final String MESSAGE_ENTERING_DELETE_PROCESS =
            "Entering delete process for shortlisted candidates...\n"
            + "Enter cancel to exit anytime.\n";

    public static final String MESSAGE_NEXT_STEP =
            "Please select a company.\n";

    /** Keeps track of the status of the delete process.
     * Returns true if process is ongoing.
     * Returns false is process is completed.
     */
    private static boolean deleteStatus;

    /** Returns the status of the delete process */
    public static boolean isDeleting() {
        return deleteStatus;
    }

    /** Sets the status of the delete process as the end */
    public static void isDoneDeleting() {
        deleteStatus = false;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);
        model.updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        model.updateFilteredCompanyJobList(PREDICATE_SHOW_ALL_JOBOFFERS);
        EventsCenter.getInstance().post(new ShowCompanyBookRequestEvent());
        deleteStatus = true;
        EventsCenter.getInstance()
                .post(new ChangeLogicStateEvent(SelectCompanyCommand.COMMAND_LOGIC_STATE_FOR_SHORTLIST_DELETE));

        return new CommandResult(MESSAGE_ENTERING_DELETE_PROCESS + MESSAGE_NEXT_STEP
                + SelectCompanyCommand.MESSAGE_USAGE);
    }
}
