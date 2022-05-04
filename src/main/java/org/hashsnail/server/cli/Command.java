package org.hashsnail.server.cli;

import java.net.Socket;
import java.util.List;
import java.util.function.Function;

public interface Command {
    String commandName = null;

    public void execute();
}