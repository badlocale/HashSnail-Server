package org.hashsnail.server.commands;

import java.util.function.Function;

public abstract class Command {
    private String commandName;
    private Function commandAction;

    public Command (String name, Function function) {
        this.commandAction = function;
        this.commandName = name;
    }

    public String getName() {
        return  commandName;
    }

    public void execute(Object o) {
        commandAction.apply(o);
    }
}
