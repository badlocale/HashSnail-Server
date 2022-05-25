package org.hashsnail.server.model.mods;

import org.hashsnail.server.net.PocketWriter;

import java.io.IOException;

public abstract class AttackMode {
    protected final Integer dataPocketIdentifier;

    protected AttackMode(int dataPocketIdentifier) {
        this.dataPocketIdentifier = dataPocketIdentifier;
    }

    public abstract void writeDataAsPocket(PocketWriter writer, String header, double entireBenchmark,
                                           double personalBenchmark) throws IOException;

    @Override
    public String toString() {
        return String.valueOf(dataPocketIdentifier);
    }

}
