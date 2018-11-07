package seedu.recruit.logic.parser;

import static seedu.recruit.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruit.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.recruit.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Ignore;
import org.junit.Test;

import seedu.recruit.logic.commands.FindCandidateCommand;
import seedu.recruit.logic.parser.exceptions.ParseException;
import seedu.recruit.testutil.CandidateContainsFindKeywordsPredicateBuilder;

public class FindCandidateCommandParserTest {

    private FindCandidateCommandParser parser = new FindCandidateCommandParser("stub");

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FindCandidateCommand.MESSAGE_USAGE));
    }

    @Test
    @Ignore
    public void parse_validArgs_returnsFindCommand() throws ParseException {
        // no leading and trailing whitespaces
        FindCandidateCommand expectedFindCommand = new FindCandidateCommand(new
                CandidateContainsFindKeywordsPredicateBuilder("n/Alice n/Bob").getCandidatePredicate());
        assertParseSuccess(parser, "n/Alice n/Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/Alice \n \t n/Bob  \t", expectedFindCommand);
    }

}
