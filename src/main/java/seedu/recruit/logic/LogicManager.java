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
import seedu.recruit.commons.core.EventsCenter;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.ui.CompanyListDetailsPanelSelectionChangedEvent;
import seedu.recruit.commons.events.ui.ShowUpdatedCompanyJobListRequestEvent;
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
    private final EmailUtil emailUtil;

    public LogicManager(Model model) {
        this.model = model;
        history = new CommandHistory();
        recruitBookParser = new RecruitBookParser();
        emailUtil = model.getEmailUtil();
    }

    @Override
    public CommandResult execute(String commandText)
            throws CommandException, ParseException, IOException, GeneralSecurityException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            Command command = recruitBookParser.parseCommand(commandText, state, emailUtil);
            return command.execute(model, history);
        } finally {
            history.add(commandText);
        }
    }

    public static void setLogicState(String newState) {
        state = new LogicState(newState);
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

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }

    @Subscribe
    private void handleCompanyListDetailsPanelSelectionChangedEvent(CompanyListDetailsPanelSelectionChangedEvent
                                                                            event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event,
                "Selection Changed to " + event.getNewSelection().getCompanyName().value));
        HashMap<String, List<String>> keywordsList = new HashMap<>();
        List<String> companyName = new ArrayList<>();
        companyName.add(event.getNewSelection().getCompanyName().toString());
        keywordsList.put("CompanyName", companyName);
        model.updateFilteredCompanyJobList(new JobOfferContainsKeywordsPredicate(keywordsList));
        EventsCenter.getInstance().post(new ShowUpdatedCompanyJobListRequestEvent(
                model.getFilteredCompanyJobList().size()));
    }
}
