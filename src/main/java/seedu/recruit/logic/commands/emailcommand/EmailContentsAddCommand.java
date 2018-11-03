package seedu.recruit.logic.commands.emailcommand;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;

import javafx.collections.ObservableList;

import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.CommandResult;
import seedu.recruit.model.Model;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.joboffer.JobOffer;

/**
 * This class handles the add sub command for email contents phase
 */
public class EmailContentsAddCommand extends EmailContentsSelectCommand {
    @Override
    @SuppressWarnings("Duplicates")
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        EmailUtil emailUtil = model.getEmailUtil();
        ArrayList<Candidate> duplicateCandidates = new ArrayList<>();
        ArrayList<JobOffer> duplicateJobOffers = new ArrayList<>();
        ArrayList<Candidate> addedCandidates = new ArrayList<>();
        ArrayList<JobOffer> addedJobOffers = new ArrayList<>();

        //check if objects being added are the same as the initial added objects
        if (emailUtil.isAreRecipientsCandidates()) {
            ObservableList<JobOffer> contents = model.getFilteredCompanyJobList();
            for (JobOffer content : contents) {
                if (!emailUtil.addJobOffer(content)) {
                    duplicateJobOffers.add(content);
                } else {
                    addedJobOffers.add(content);
                }
            }
        } else {
            ObservableList<Candidate> contents = model.getFilteredCandidateList();
            for (Candidate content : contents) {
                if (!emailUtil.addCandidate(content)) {
                    duplicateCandidates.add(content);
                } else {
                    addedCandidates.add(content);
                }
            }
        }

        //Generate duplicate string (if any)
        boolean hasDuplicates = false;
        String duplicates = "Unable to add the following because it already has been added before:\n";
        if (duplicateCandidates.size() != 0 || duplicateJobOffers.size() != 0) {
            if (!emailUtil.isAreRecipientsCandidates()) {
                for (Candidate duplicateCandidate : duplicateCandidates) {
                    duplicates += duplicateCandidate.getName().toString();
                    duplicates += "\n";
                }
            } else {
                for (JobOffer duplicateJobOffer : duplicateJobOffers) {
                    duplicates += emailUtil.getContentJobOfferName(duplicateJobOffer);
                    duplicates += "\n";
                }
            }
            hasDuplicates = true;
        }

        //Generate recipients string
        String contents = "Contents added:\n";
        if (emailUtil.isAreRecipientsCandidates()) {
            for (JobOffer addedJobOffer : addedJobOffers) {
                contents += emailUtil.getContentJobOfferName(addedJobOffer);
                contents += "\n";
            }
        } else {
            for (Candidate addedCandidate : addedCandidates) {
                contents += addedCandidate.getName().toString();
                contents += "\n";
            }
        }

        //Generate output string
        String output = "";

        if (hasDuplicates) {
            output += duplicates;
        }

        if (!contents.equals("Contents added:\n")) {
            output += contents;
        }

        output += EmailRecipientsSelectCommand.MESSAGE_USAGE;
        return new CommandResult(output);
    }
}
