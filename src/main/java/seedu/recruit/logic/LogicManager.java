package seedu.recruit.logic;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;

import seedu.recruit.commons.core.ComponentManager;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.ui.CompanyListDetailsPanelSelectionChangedEvent;
import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.commands.Command;
import seedu.recruit.logic.commands.CommandResult;
import seedu.recruit.logic.commands.exceptions.CommandException;
import seedu.recruit.logic.parser.RecruitBookParser;
import seedu.recruit.logic.parser.exceptions.ParseException;
import seedu.recruit.model.Model;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.joboffer.JobOfferContainsKeywordsPredicate;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {

    private static LogicState state = new LogicState("primary");

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final CommandHistory history;
    private final RecruitBookParser recruitBookParser;

    public LogicManager(Model model) {
        this.model = model;
        history = new CommandHistory();
        recruitBookParser = new RecruitBookParser();
    }

    @Override
    public CommandResult execute(String commandText)
            throws CommandException, ParseException, IOException, GeneralSecurityException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            EmailUtil emailUtil = model.getEmailUtil();
            Command command = recruitBookParser.parseCommand(commandText, state, emailUtil);
            return command.execute(model, history);
        } finally {
            history.add(commandText);
        }
    }

    @Override
    public ObservableList<Candidate> getFilteredPersonList() {
        return model.getFilteredCandidateList();
    }

    @Override
    public ObservableList<Company> getFilteredCompanyList() {
        return model.getFilteredCompanyList();
    }

    @Override
    public ObservableList<JobOffer> getFilteredCompanyJobList() {
        return model.getFilteredCompanyJobList();
    }


    @Subscribe
    private void handleCompanyListDetailsPanelSelectionChangedEvent(CompanyListDetailsPanelSelectionChangedEvent
                                                                                event) {
        HashMap<String, List<String>> keywordsList = new HashMap<>();
        List<String> companyName = new ArrayList<>();
        companyName.add(event.getNewSelection().getCompanyName().toString());
        keywordsList.put("CompanyName", companyName);
        model.updateFilteredCompanyJobList(new JobOfferContainsKeywordsPredicate(keywordsList));
    }

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }

    public static void setLogicState(String newState) {
        state = new LogicState(newState);
    }
}
