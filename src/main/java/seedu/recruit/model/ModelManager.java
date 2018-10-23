package seedu.recruit.model;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.recruit.commons.core.ComponentManager;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.model.CandidateBookChangedEvent;
import seedu.recruit.commons.events.model.CompanyBookChangedEvent;
import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.parser.Prefix;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.company.CompanyName;
import seedu.recruit.model.joboffer.JobOffer;

/**
 * Represents the in-memory model of the recruit book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedCandidateBook versionedCandidateBook;
    private final VersionedCompanyBook versionedCompanyBook;
    private final FilteredList<Candidate> filteredCandidates;
    private final FilteredList<Company> filteredCompanies;
    private final FilteredList<JobOffer> filteredJobs;
    private EmailUtil emailUtil;

    /**
     * Initializes a ModelManager with the given candidateBook and userPrefs.
     */
    public ModelManager(ReadOnlyCandidateBook candidateBook, ReadOnlyCompanyBook companyBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(candidateBook, userPrefs);

        logger.fine("Initializing with recruit book: " + candidateBook + " and user prefs " + userPrefs);

        EmailUtil.setEmailSettings(userPrefs.getEmailSettings());
        versionedCandidateBook = new VersionedCandidateBook(candidateBook);
        versionedCompanyBook = new VersionedCompanyBook(companyBook);
        filteredCandidates = new FilteredList<>(versionedCandidateBook.getCandidateList());
        filteredCompanies = new FilteredList<>(versionedCompanyBook.getCompanyList());
        filteredJobs = new FilteredList<>(versionedCompanyBook.getCompanyJobList());
        emailUtil = new EmailUtil();
    }

    public ModelManager() {
        this(new CandidateBook(), new CompanyBook(), new UserPrefs());
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedCandidateBook.equals(other.versionedCandidateBook)
                && filteredCandidates.equals(other.filteredCandidates)
                && versionedCompanyBook.equals(other.versionedCompanyBook)
                && filteredCompanies.equals(other.filteredCompanies);
    }

    // ================================== CandidateBook functions ====================================== //

    @Override
    public void resetCandidateData(ReadOnlyCandidateBook newData) {
        versionedCandidateBook.resetData(newData);
        indicateCandidateBookChanged();
    }

    @Override
    public ReadOnlyCandidateBook getCandidateBook() {
        return versionedCandidateBook;
    }

    /**
     * Raises an event to indicate the model has changed
     */
    private void indicateCandidateBookChanged() {
        raise(new CandidateBookChangedEvent(versionedCandidateBook));
    }

    @Override
    public boolean hasCandidate(Candidate candidate) {
        requireNonNull(candidate);
        return versionedCandidateBook.hasPerson(candidate);
    }

    @Override
    public void deleteCandidate(Candidate target) {
        versionedCandidateBook.removePerson(target);
        indicateCandidateBookChanged();
    }

    @Override
    public void addCandidate(Candidate candidate) {
        versionedCandidateBook.addPerson(candidate);
        updateFilteredCandidateList(PREDICATE_SHOW_ALL_PERSONS);
        indicateCandidateBookChanged();
    }

    @Override
    public void updateCandidate(Candidate target, Candidate editedCandidate) {
        requireAllNonNull(target, editedCandidate);

        versionedCandidateBook.updatePerson(target, editedCandidate);
        indicateCandidateBookChanged();
    }

    @Override
    public void sortCandidates(Prefix prefix) {
        versionedCandidateBook.sortCandidates(prefix);
        indicateCandidateBookChanged();
    }

    //=========== Filtered Candidate List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Candidate} backed by the internal list of
     * {@code versionedCandidateBook}
     */
    @Override
    public ObservableList<Candidate> getFilteredCandidateList() {
        return FXCollections.unmodifiableObservableList(filteredCandidates);
    }

    @Override
    public void updateFilteredCandidateList(Predicate<Candidate> predicate) {
        requireNonNull(predicate);
        filteredCandidates.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoCandidateBook() {
        return versionedCandidateBook.canUndo();
    }

    @Override
    public boolean canRedoCandidateBook() {
        return versionedCandidateBook.canRedo();
    }

    @Override
    public void undoCandidateBook() {
        versionedCandidateBook.undo();
        indicateCandidateBookChanged();
    }

    @Override
    public void redoCandidateBook() {
        versionedCandidateBook.redo();
        indicateCandidateBookChanged();
    }

    @Override
    public void commitCandidateBook() {
        versionedCandidateBook.commit();
    }


    // ================================== CompanyBook functions ===================================== //

    @Override
    public void resetCompanyData(ReadOnlyCompanyBook newData) {
        versionedCompanyBook.resetData(newData);
        indicateCompanyBookChanged();
    }

    @Override
    public ReadOnlyCompanyBook getCompanyBook() {
        return versionedCompanyBook;
    }

    /**
     * Raises an event to indicate the model has changed
     */
    private void indicateCompanyBookChanged() {
        raise(new CompanyBookChangedEvent(versionedCompanyBook));
    }

    @Override
    public boolean hasCompany(Company company) {
        requireNonNull(company);
        return versionedCompanyBook.hasCompany(company);
    }

    @Override
    public void deleteCompany(Company target) {
        versionedCompanyBook.removeCompany(target);
        indicateCompanyBookChanged();
    }

    @Override
    public void addCompany(Company company) {
        versionedCompanyBook.addCompany(company);
        updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        indicateCompanyBookChanged();
    }

    @Override
    public int getCompanyIndexFromName(CompanyName companyName) {
        return versionedCompanyBook.getCompanyIndexFromName(companyName);
    }

    @Override
    public Company getCompanyFromIndex(int index) {
        return versionedCompanyBook.getCompanyFromIndex(index);
    }

    @Override
    public void updateCompany(Company target, Company editedCompany) {
        requireAllNonNull(target, editedCompany);

        versionedCompanyBook.updateCompany(target, editedCompany);
        indicateCompanyBookChanged();
    }

    //=========== Filtered Company List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Company} backed by the internal list of
     * {@code versionedCompanyBook}
     */
    @Override
    public ObservableList<Company> getFilteredCompanyList() {
        return FXCollections.unmodifiableObservableList(filteredCompanies);
    }

    @Override
    public void updateFilteredCompanyList(Predicate<Company> predicate) {
        requireNonNull(predicate);
        filteredCompanies.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoCompanyBook() {
        return versionedCompanyBook.canUndo();
    }

    @Override
    public boolean canRedoCompanyBook() {
        return versionedCompanyBook.canRedo();
    }

    @Override
    public void undoCompanyBook() {
        versionedCompanyBook.undo();
        indicateCompanyBookChanged();
    }

    @Override
    public void redoCompanyBook() {
        versionedCompanyBook.redo();
        indicateCompanyBookChanged();
    }

    @Override
    public void commitCompanyBook() {
        versionedCompanyBook.commit();
    }

    // ================================== Job Offer functions ===================================== //

    @Override
    public void addJobOffer(CompanyName companyName, JobOffer jobOffer) {
        requireAllNonNull(companyName, jobOffer);
        versionedCompanyBook.addJobOfferToCompany(companyName, jobOffer);
        indicateCompanyBookChanged();
    }

    @Override
    public void deleteJobOffer(JobOffer jobOffer) {
        requireNonNull(jobOffer);
        versionedCompanyBook.deleteJobOffer(jobOffer);
        indicateCompanyBookChanged();
    }

    /**
     * Returns an unmodifiable view of the job lists of all companies {@code Company} backed by the internal list of
     * {@code versionedCompanyBook}
     */
    @Override
    public ObservableList<JobOffer> getFilteredCompanyJobList() {
        return FXCollections.unmodifiableObservableList(filteredJobs);
    }

    @Override
    public void updateFilteredCompanyJobList(Predicate<JobOffer> predicate) {
        requireNonNull(predicate);
        filteredJobs.setPredicate(predicate);
    }

    // ================================== Email Command functions ===================================== //

    public EmailUtil getEmailUtil() {
        return emailUtil;
    }

    public void setEmailUtil(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    /**
     * Returns a concatenated string of names of job offers for email select recipients command
     */
    @Override
    public String getFilteredRecipientJobOfferNames() {
        StringBuilder output = new StringBuilder();
        for (JobOffer jobOffer : filteredJobs) {
            output.append(jobOffer.getCompanyName().toString());
            output.append(" regarding job offer: ");
            output.append(jobOffer.getJob().toString());
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * @param duplicateJobOffers arraylist of duplicate job offers
     * @return concatenated string of names of job offers for
     * email select recipients command minus specified job offers
     */
    @Override
    public String getFilteredRecipientJobOfferNames(ArrayList<JobOffer> duplicateJobOffers) {
        boolean hasDuplicate;
        StringBuilder output = new StringBuilder();
        for (JobOffer jobOffer : filteredJobs) {
            hasDuplicate = false;
            for (JobOffer duplicateJobOffer : duplicateJobOffers) {
                if (jobOffer.isSameJobOffer(duplicateJobOffer)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (!hasDuplicate) {
                output.append(jobOffer.getCompanyName().toString());
                output.append(" regarding job offer: ");
                output.append(jobOffer.getJob().toString());
                output.append("\n");
            }
        }
        return output.toString();
    }

    /**
     * Returns a concatenated string of names of job offers for email select contents command
     */
    @Override
    public String getFilteredContentJobOfferNames() {
        StringBuilder output = new StringBuilder();
        for (JobOffer jobOffer : filteredJobs) {
            output.append(jobOffer.getJob().toString());
            output.append(" at ");
            output.append(jobOffer.getCompanyName().toString());
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * @param duplicateJobOffers arraylist of duplicate joboffers
     * @return a concatenated string of names of job offers
     * for email select contents command minus specified job offers
     */

    @Override
    public String getFilteredContentJobOfferNames(ArrayList<JobOffer> duplicateJobOffers) {
        boolean hasDuplicate;
        StringBuilder output = new StringBuilder();
        for (JobOffer jobOffer : filteredJobs) {
            hasDuplicate = false;
            for (JobOffer duplicateJobOffer : duplicateJobOffers) {
                if (jobOffer.isSameJobOffer(duplicateJobOffer)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (!hasDuplicate) {
                output.append(jobOffer.getJob().toString());
                output.append(" at ");
                output.append(jobOffer.getCompanyName().toString());
                output.append("\n");
            }
        }
        return output.toString();
    }

    /**
     * Returns a concatenated string of names of candidates for email command
     */
    @Override
    public String getFilteredCandidateNames() {
        StringBuilder output = new StringBuilder();
        for (Candidate candidate : filteredCandidates) {
            output.append(candidate.getName().toString());
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * @param duplicateCandidates Arraylist of duplicate candidates
     * @return a concatenated string of names of candidates for email command minus duplicate candidates
     */
    @Override
    public String getFilteredCandidateNames(ArrayList<Candidate> duplicateCandidates) {
        boolean hasDuplicate;
        StringBuilder output = new StringBuilder();
        for (Candidate candidate : filteredCandidates) {
            hasDuplicate = false;
            for (Candidate duplicateCandidate : duplicateCandidates) {
                if (candidate.isSamePerson(duplicateCandidate)) {
                    hasDuplicate = true;
                    break;
                }
            }
            if (!hasDuplicate) {
                output.append(candidate.getName().toString());
                output.append("\n");
            }
        }
        return output.toString();
    }
}