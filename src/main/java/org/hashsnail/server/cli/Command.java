package org.hashsnail.server.cli;

import java.util.function.Function;

public abstract class Command {
    private String commandName;

    public Command (String name, Function function) {
        this.commandName = name;
    }

    public String getName() {
        return  commandName;
    }

    public void execute(Object o) {

    }
}