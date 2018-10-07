package seedu.address.logic.commands;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javafx.collections.ObservableList;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import seedu.address.commons.core.Email;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.RecruitBookEntries;
import seedu.address.model.candidate.Candidate;
import seedu.address.model.joboffer.Job;

/**
 * Email Command (Work in progress)
 */
public class EmailCommand extends Command {

    public static final String COMMAND_WORD = "email";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": In Progress";
    private static final String EMAIL_SUCCESS = "Email(s) sent successfully!";
    private static final String EMAIL_FAILURE = "Email(s) not successfully sent!";

    public static ObservableList<?extends RecruitBookEntries> recipientCandidates;
    /*
    public static ObservableList<Candidate> contentCandidates;
    public static ObservableList<Job> recipientJobs;
    public static ObservableList<Job> contentJobs;
    */

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws GeneralSecurityException {
        MimeMessage email;
        String result;
        recipientCandidates = model.getFilteredCandidateList();
        try {
            email = Email.createEmail("cs2113f094@gmail.com", "cs2113f094@gmail.com",
                    "testing123", "Hello, I am testing!");
            Email.sendMessage(Email.init(), "me", email);
            result = EMAIL_SUCCESS;

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            result = EMAIL_FAILURE;
        }

        return new CommandResult(result);
    }

}
