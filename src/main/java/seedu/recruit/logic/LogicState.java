package seedu.recruit.logic;

/**
 * Represents the state of logic of LogicManager
 */

public class LogicState {
    public final String nextCommand;

    public LogicState(String nextCommand) {
        this.nextCommand = nextCommand;
    }

    @Override
    public String toString() {
        return nextCommand;
    }
}
