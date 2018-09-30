package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.candidate.Address;
import seedu.address.model.candidate.Age;
import seedu.address.model.candidate.Candidate;
import seedu.address.model.candidate.Education;
import seedu.address.model.candidate.Email;
import seedu.address.model.candidate.Gender;
import seedu.address.model.candidate.Job;
import seedu.address.model.candidate.Name;
import seedu.address.model.candidate.Phone;
import seedu.address.model.candidate.Salary;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Candidate objects.
 */
public class PersonBuilder {
    //MUST EDIT
    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_GENDER = "F";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "alice@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private Gender gender;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;

    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        gender = new Gender(DEFAULT_GENDER);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code candidateToCopy}.
     */
    public PersonBuilder(Candidate candidateToCopy) {
        name = candidateToCopy.getName();
        gender = candidateToCopy.getGender();
        phone = candidateToCopy.getPhone();
        email = candidateToCopy.getEmail();
        address = candidateToCopy.getAddress();
        tags = new HashSet<>(candidateToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Candidate} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Gender} of the {@code Candidate} that we are building.
     */
    public PersonBuilder withGender(String gender) {
        this.gender = new Gender(gender);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Candidate} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Candidate} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Candidate} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Candidate} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    public Candidate build() {
        return new Candidate(name, gender, phone, email, address, tags);
    }

}
