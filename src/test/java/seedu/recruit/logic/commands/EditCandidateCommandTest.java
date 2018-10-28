package seedu.recruit.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.recruit.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.recruit.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.recruit.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.recruit.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.recruit.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.recruit.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.recruit.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.recruit.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.recruit.testutil.TypicalIndexes.INDEX_FIRST;
import static seedu.recruit.testutil.TypicalIndexes.INDEX_SECOND;
import static seedu.recruit.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Ignore;
import org.junit.Test;

import seedu.recruit.commons.core.Messages;
import seedu.recruit.commons.core.index.Index;
import seedu.recruit.logic.CommandHistory;
import seedu.recruit.logic.commands.EditCandidateCommand.EditPersonDescriptor;
import seedu.recruit.model.CandidateBook;
import seedu.recruit.model.CompanyBook;
import seedu.recruit.model.Model;
import seedu.recruit.model.ModelManager;
import seedu.recruit.model.UserPrefs;
import seedu.recruit.model.candidate.Candidate;
import seedu.recruit.testutil.CandidateBuilder;
import seedu.recruit.testutil.EditPersonDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCandidateBookCommand and RedoCandidateBookCommand)
 * and unit tests for EditCandidateCommand.
 */
@Ignore
public class EditCandidateCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new CompanyBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Candidate editedCandidate = new CandidateBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedCandidate).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST, descriptor);

        String expectedMessage = String.format(editCandidateCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedCandidate);

        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), model.getCompanyBook(),
                new UserPrefs());
        expectedModel.updateCandidate(model.getFilteredCandidateList().get(0), editedCandidate);
        expectedModel.commitCandidateBook();

        assertCommandSuccess(editCandidateCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredCandidateList().size());
        Candidate lastCandidate = model.getFilteredCandidateList().get(indexLastPerson.getZeroBased());

        CandidateBuilder personInList = new CandidateBuilder(lastCandidate);
        Candidate editedCandidate = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(editCandidateCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedCandidate);

        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), model.getCompanyBook(),
                new UserPrefs());
        expectedModel.updateCandidate(lastCandidate, editedCandidate);
        expectedModel.commitCandidateBook();

        assertCommandSuccess(editCandidateCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST,
                new EditPersonDescriptor());
        Candidate editedCandidate = model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased());

        String expectedMessage = String.format(EditCandidateCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedCandidate);

        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), model.getCompanyBook(),
                new UserPrefs());
        expectedModel.commitCandidateBook();

        assertCommandSuccess(editCandidateCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST);

        Candidate candidateInFilteredList = model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased());
        Candidate editedCandidate = new CandidateBuilder(candidateInFilteredList).withName(VALID_NAME_BOB).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(editCandidateCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedCandidate);

        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), model.getCompanyBook(),
                new UserPrefs());
        expectedModel.updateCandidate(model.getFilteredCandidateList().get(0), editedCandidate);
        expectedModel.commitCandidateBook();

        assertCommandSuccess(editCandidateCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Candidate firstCandidate = model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstCandidate).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_SECOND, descriptor);

        assertCommandFailure(editCandidateCommand, model, commandHistory,
                editCandidateCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST);

        // edit candidate in filtered list into a duplicate in recruit book
        Candidate candidateInList = model.getCandidateBook().getCandidateList().get(INDEX_SECOND.getZeroBased());
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST,
                new EditPersonDescriptorBuilder(candidateInList).build());

        assertCommandFailure(editCandidateCommand, model, commandHistory,
                editCandidateCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCandidateList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCandidateCommand, model, commandHistory,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of recruit book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST);
        Index outOfBoundIndex = INDEX_SECOND;
        // ensures that outOfBoundIndex is still in bounds of recruit book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getCandidateBook().getCandidateList().size());

        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCandidateCommand, model, commandHistory,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Candidate editedCandidate = new CandidateBuilder().build();
        Candidate candidateToEdit = model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedCandidate).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST, descriptor);
        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), new CompanyBook(),
                new UserPrefs());
        expectedModel.updateCandidate(candidateToEdit, editedCandidate);
        expectedModel.commitCandidateBook();

        // edit -> first candidate edited
        editCandidateCommand.execute(model, commandHistory);

        // undo -> reverts Candidatebook back to previous state and filtered candidate list to show all persons
        expectedModel.undoCandidateBook();
        assertCommandSuccess(new UndoCandidateBookCommand(), model, commandHistory,
                UndoCandidateBookCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first candidate edited again
        expectedModel.redoCandidateBook();
        assertCommandSuccess(new RedoCandidateBookCommand(), model, commandHistory,
                RedoCandidateBookCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCandidateList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(outOfBoundIndex, descriptor);

        // execution failed -> recruit book state not added into model
        assertCommandFailure(editCandidateCommand, model, commandHistory,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

        // single recruit book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCandidateBookCommand(), model, commandHistory,
                UndoCandidateBookCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCandidateBookCommand(), model, commandHistory,
                RedoCandidateBookCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Edits a {@code Candidate} from a filtered list.
     * 2. Undo the edit.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously edited candidate in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the edit. This ensures {@code RedoCandidateBookCommand} edits the candidate object regardless of
     * indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_samePersonEdited() throws Exception {
        Candidate editedCandidate = new CandidateBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedCandidate).build();
        EditCandidateCommand editCandidateCommand = new EditCandidateCommand(INDEX_FIRST, descriptor);
        Model expectedModel = new ModelManager(new CandidateBook(model.getCandidateBook()), new CompanyBook(),
                new UserPrefs());

        showPersonAtIndex(model, INDEX_SECOND);
        Candidate candidateToEdit = model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased());
        expectedModel.updateCandidate(candidateToEdit, editedCandidate);
        expectedModel.commitCandidateBook();

        // edit -> edits second candidate in unfiltered candidate list / first candidate in filtered candidate list
        editCandidateCommand.execute(model, commandHistory);

        // undo -> reverts candidatebook back to previous state and filtered candidate list to show all persons
        expectedModel.undoCandidateBook();
        assertCommandSuccess(new UndoCandidateBookCommand(), model, commandHistory,
                UndoCandidateBookCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(model.getFilteredCandidateList().get(INDEX_FIRST.getZeroBased()), candidateToEdit);
        // redo -> edits same second candidate in unfiltered candidate list
        expectedModel.redoCandidateBook();
        assertCommandSuccess(new RedoCandidateBookCommand(), model, commandHistory,
                RedoCandidateBookCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        final EditCandidateCommand standardCommand = new EditCandidateCommand(INDEX_FIRST, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCandidateCommand commandWithSameValues = new EditCandidateCommand(INDEX_FIRST, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCandidateBookCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCandidateCommand(INDEX_SECOND, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCandidateCommand(INDEX_FIRST, DESC_BOB)));
    }

}
