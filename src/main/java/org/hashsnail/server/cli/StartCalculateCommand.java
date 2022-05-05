package org.hashsnail.server.cli;

import org.hashsnail.server.net.ServerSession;

import java.util.List;

public final class StartCalculateCommand implements Command {
    private List<ServerSession> sessions;

    public StartCalculateCommand(List<ServerSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void execute() {
        for (ServerSession session: sessions) {
            session.startCalculate();
        }
    }
}
