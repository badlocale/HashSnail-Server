package org.hashsnail.server.model.mods;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AttackMode {
    protected final int modeValue;

    protected AttackMode(int modeValue) {
        this.modeValue = modeValue;
    }

    public abstract void writeNextRange(OutputStream out, double entireBenchmark, double personalBenchmark)
            throws IOException;

    @Override
    public String toString() {
        return String.valueOf(modeValue);
    }
}
