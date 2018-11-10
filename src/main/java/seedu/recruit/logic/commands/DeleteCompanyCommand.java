package seedu.recruit.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.commons.events.ui.ShowCompanyBookRequestEvent;
import seedu.recruit.commons.events.ui.ShowUpdatedCompanyJobListRequestEvent;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.model.Model;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.company.Company;

/**
 * Deletes a company identified using its displayed index from the recruit book.
 */

public class DeleteCompanyCommand extends Command {

    public static final String COMMAND_WORD = "deleteC";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the company(s) identified by the index number(s) used in the displayed company list.\n"
            + "Parameters: INDEX,INDEX-INDEX ... (INDEX must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1,2,7-9";

    public static final String MESSAGE_DELETE_COMPANY_SUCCESS = "Deleted Company(s):\n%1$s";

    private final Set<Index> targetIndexes;

    public DeleteCompanyCommand(Set<Index> targetIndexes) {
        this.targetIndexes = targetIndexes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history, UserPrefs userPrefs) throws CommandException {
        requireNonNull(model);
        EventsCenter.getInstance().post(new ShowCompanyBookRequestEvent());

        List<Company> lastShownList = model.getFilteredCompanyList();
        StringBuilder deletedCompanies = new StringBuilder();

        //Check if any of the specified indexes are invalid
        for (Index index: targetIndexes) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
            }
        }

        for (Index index: targetIndexes) {
            Company companyToDelete = lastShownList.get(index.getZeroBased());
            model.deleteCompany(companyToDelete);
            deletedCompanies.append(companyToDelete + "\n");
        }

        model.commitRecruitBook();
        EventsCenter.getInstance().post(new ShowUpdatedCompanyJobListRequestEvent(
                model.getFilteredCompanyJobList().size()));
        return new CommandResult(String.format(MESSAGE_DELETE_COMPANY_SUCCESS, deletedCompanies));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCompanyCommand // instanceof handles nulls
                && targetIndexes.equals(((DeleteCompanyCommand) other).targetIndexes)); // state check
    }


}
