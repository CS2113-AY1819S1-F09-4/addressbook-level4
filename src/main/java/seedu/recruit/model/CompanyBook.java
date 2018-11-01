package seedu.recruit.model;

import static java.util.Objects.requireNonNull;
import static seedu.recruit.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.recruit.logic.parser.Prefix;
import seedu.recruit.model.company.Company;
import seedu.recruit.model.company.CompanyName;
import seedu.recruit.model.company.UniqueCompanyList;
import seedu.recruit.model.joboffer.JobOffer;
import seedu.recruit.model.joboffer.UniqueJobList;


/**
 * Wraps all data at the CompanyBook level
 * Duplicates are not allowed (by .isSameCompany comparison)
 */

public class CompanyBook implements ReadOnlyCompanyBook {
    private final UniqueCompanyList companyList;
    private final UniqueJobList companyJobList;

    {
        companyList = new UniqueCompanyList();
        companyJobList = new UniqueJobList();
    }

    public CompanyBook() {}

    /**
     * Creates a CompanyBook using the companies in the {@code toBeCopied}
     */
    public CompanyBook(ReadOnlyCompanyBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }


    //// list overwrite operations

    /**
     * Replaces the contents of the company list with {@code companyList}.
     * {@code companyList} must not contain duplicate companies.
     */
    public void setCompanyList(List<Company> companyList) {
        this.companyList.setCompanyList(companyList);
    }

    public void setCompanyList(UniqueCompanyList companyList) {
        this.companyList.setCompanyList(companyList);
    }

    /**
     * Resets the existing data of this {@code CompanyBook} with {@code newData}.
     */
    public void resetData(ReadOnlyCompanyBook newData) {
        requireNonNull(newData);

        setCompanyList(newData.getCompanyList());

        companyJobList.setJobOffers(newData.getCompanyJobList());
    }

    //// company -level operations

    /**
     * Returns true if a company with the same identity as {@code company} exists in the recruit book.
     */
    public boolean hasCompany(Company company) {
        requireNonNull(company);
        return companyList.contains(company);
    }

    /**
     * Adds a company to the recruit book.
     * The company must not already exist in the recruit book.
     */
    public void addCompany(Company p) {
        companyList.add(p);
        for (JobOffer jobOffer : p.getUniqueJobList()) {
            companyJobList.add(jobOffer);
        }
    }

    /**
     * Returns company index if company with @param companyName exists in CompanyBook
     * and -1 otherwise
     */
    public int getCompanyIndexFromName(CompanyName companyName) {
        return companyList.getCompanyIndexFromName(companyName);
    }

    /**
     * Returns the company object with the given index
     */

    public Company getCompanyFromIndex(int index) {
        return companyList.getCompanyFromIndex(index);
    }


    /**
     * Replaces the given company {@code target} in the list with {@code editedCompany}.
     * {@code target} must exist in the recruit book.
     * The company identity of {@code editedCompany} must not be the same as another existing company in the
     * CompanyBook.
     */
    public void updateCompany(Company target, Company editedCompany) {
        requireNonNull(editedCompany);
        companyList.setCompany(target, editedCompany);
    }

    /**
     * Sorts the company list
     */
    public void sortCompanies(Prefix prefix) {
        String prefixString = prefix.toString();
        switch (prefixString) {
        case "c/":
            companyList.sortByCompanyName();
            break;
        case "e/":
            companyList.sortByEmail();
            break;
        default:
            companyList.sortInReverse();
        }
    }

    /**
     * Removes {@code key} from this {@code CompanyBook}.
     * {@code key} must exist in the CompanyBook.
     */
    public void removeCompany(Company key) {
        companyList.remove(key);
        List<JobOffer> jobOffersToRemove = new ArrayList<>();
        for (JobOffer jobOffer: companyJobList) {
            if (jobOffer.getCompanyName().equals(key.getCompanyName())) {
                jobOffersToRemove.add(jobOffer);
            }
        }
        companyJobList.removeAll(jobOffersToRemove);
    }

    // job offer level operations

    /**
     * Adds a job offer to an existing company in the CompanyBook
     */
    public void addJobOfferToCompany(CompanyName companyName, JobOffer jobOffer) {
        companyList.addJobOfferToCompany(companyName, jobOffer);
        companyJobList.add(jobOffer);
    }

    /**
     * Returns true if a company has a job offer with the same identity as {@code jobOffer} exists in the company book.
     */
    public boolean hasJobOffer(CompanyName companyName, JobOffer jobOffer) {
        requireAllNonNull(companyName, jobOffer);
        Company company = getCompanyFromIndex(getCompanyIndexFromName(companyName));
        return company.getUniqueJobList().contains(jobOffer);
    }

    /**
     * Replaces the given job offer {@code target} in the list with {@code editedJobOffer}.
     * {@code target} must exist in the company job list{@code company}.
     * The job offer identity of {@code editedJobOffer} must not be the same as another existing job offer in the
     * same company{@code company}.
     */
    public void updateJobOfferInCompany(Company company, JobOffer target, JobOffer editedJobOffer) {
        requireAllNonNull(company, target, editedJobOffer);
        company.getUniqueJobList().setJobOffer(target, editedJobOffer);
    }

    /**
     * Replaces the given job offer {@code target} in the list with {@code editedJobOffer}.
     * {@code target} must exist in the company book.
     * The job offer identity of {@code editedJobOffer} must not be the same as another existing job offer in the
     * company book.
     */
    public void updateJobOffer(JobOffer target, JobOffer editedJobOffer) {
        requireAllNonNull(target, editedJobOffer);
        companyJobList.setJobOffer(target, editedJobOffer);
    }

    /**
     * Deletes a job offer from an existing company in the CompanyBook.
     * @param jobOffer must exist inside the CompanyBook
     */
    public void removeJobOffer(JobOffer jobOffer) {
        companyList.removeJobOffer(jobOffer);
        companyJobList.remove(jobOffer);

    }

    //// util methods

    @Override
    public String toString() {
        return companyList.asUnmodifiableObservableList().size() + " companyList";
        // TODO: refine later
    }

    @Override
    public ObservableList<Company> getCompanyList() {
        return companyList.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<JobOffer> getCompanyJobList() {
        return companyJobList.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CompanyBook // instanceof handles nulls
                && companyList.equals(((CompanyBook) other).companyList));
    }

    @Override
    public int hashCode() {
        return companyList.hashCode();
    }

}
