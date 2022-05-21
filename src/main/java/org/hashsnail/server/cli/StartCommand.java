package org.hashsnail.server.cli;

import org.hashsnail.server.Server;

public final class StartCommand implements Command {
    @Override
    public void execute() {
        Server.startSessions();
    }
}
