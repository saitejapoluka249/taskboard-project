package com.taskboard.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple history container (no undo implemented, but ready for it).
 */
public class CommandHistory {
    private final List<Command> executed = new ArrayList<>();

    public void add(Command command) {
        executed.add(command);
    }

    public List<Command> getExecuted() {
        return executed;
    }
}
