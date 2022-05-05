package org.hashsnail.server.cli;

import org.hashsnail.server.Server;
import org.hashsnail.server.net.ServerSession;

import java.util.List;

public final class StartBenchmarkCommand implements Command {
    private List<ServerSession> sessions;

    public StartBenchmarkCommand(List<ServerSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void execute() {
        for (ServerSession session: sessions) {
            session.startBenchmark();
        }
    }
}
