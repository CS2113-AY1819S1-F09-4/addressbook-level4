package seedu.recruit.logic.parser;

import static seedu.recruit.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.recruit.commons.core.index.Index;
import seedu.recruit.logic.commands.SelectCompanyCommand;
import seedu.recruit.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SelectCompanyCommand object
 */
public class SelectCompanyCommandParser implements Parser<SelectCompanyCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SelectCompanyCommand
     * and returns an SelectCompanyCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SelectCompanyCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new SelectCompanyCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCompanyCommand.MESSAGE_USAGE), pe);
        }
    }
}
