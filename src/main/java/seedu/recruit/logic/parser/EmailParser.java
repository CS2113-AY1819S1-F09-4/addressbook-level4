package seedu.recruit.logic.parser;

import static seedu.recruit.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import seedu.recruit.commons.util.EmailUtil;
import seedu.recruit.logic.LogicState;
import seedu.recruit.logic.commands.Command;
import seedu.recruit.logic.commands.ListCommand;
import seedu.recruit.logic.commands.emailcommand.EmailSelectContentsCommand;
import seedu.recruit.logic.commands.emailcommand.EmailSelectRecipientsCommand;
import seedu.recruit.logic.commands.emailcommand.EmailSendCommand;
import seedu.recruit.logic.parser.exceptions.ParseException;

/**
 * Parser for the email commands to reduce clutter inside RecruitBookParser
 */
public class EmailParser {

    /**
     * Constructor to parse commands if the logic state is something email related
     * @param commandWord name of the command taken from RecruitBookParser
     * @param arguments arguments of the command taken from RecruitBookParser
     * @param state logic state
     * @param emailUtil emailUtil to get boolean value of isAreRecipientsCandidates.
     * @return the email command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String commandWord, String arguments, LogicState state, EmailUtil emailUtil)
            throws ParseException {
        //Email command set recipients step
        if (state.nextCommand.equals(EmailSelectRecipientsCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case "next":
                return new EmailSelectRecipientsCommand();

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }

        //Email command set contents step. Allow certain commands depending whether
        //recipients are candidates or job offers.
        } else if (state.nextCommand.equals(EmailSelectContentsCommand.COMMAND_LOGIC_STATE)
                && emailUtil.isAreRecipientsCandidates()) {
            switch (commandWord) {

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case "next":
                return new EmailSelectContentsCommand();

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        } else if (state.nextCommand.equals(EmailSelectContentsCommand.COMMAND_LOGIC_STATE)
                && !emailUtil.isAreRecipientsCandidates()) {
            switch (commandWord) {

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case "next":
                return new EmailSelectContentsCommand();

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }

        //Email command send step. Choose to send or to go back to edit recipients/contents
        } else if (state.nextCommand.equals(EmailSendCommand.COMMAND_LOGIC_STATE)) {
            switch (commandWord) {

            case "send":
                return new EmailSendCommand();

            default:
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        } else {
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}