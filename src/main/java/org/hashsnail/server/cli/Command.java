package org.hashsnail.server.cli;

public interface Command {
    String commandName = null;

    public void execute();
}