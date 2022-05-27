package org.hashsnail.server.model.mods;

import org.hashsnail.server.Server;
import org.hashsnail.server.model.range.PasswordRange;
import org.hashsnail.server.net.PocketTypes;
import org.hashsnail.server.net.PocketWriter;

import java.io.IOException;

public final class AttackByMask extends AttackMode {
    private volatile float previousPartEnd = 0;
    private final PasswordRange passwordRange;

    public AttackByMask(String rawMask) {
        super(PocketTypes.MASK_DATA.ordinal());

        this.passwordRange = new PasswordRange(rawMask.toCharArray());
    }

    public void writeDataAsPocket(PocketWriter writer, String header, double entireBenchmark, double personalBenchmark)
            throws IOException {
        double proportion = entireBenchmark / personalBenchmark;

        writer.writeData(
            String.valueOf(PocketTypes.MASK_DATA.ordinal()),
            passwordRange.toString(),
            String.valueOf(Server.getAlgorithm().getCodeNumber()),
            nextPasswordRange(proportion));
    }

    public synchronized String nextPasswordRange(double delta) { //todo
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(passwordRange.subdivide(previousPartEnd));
        stringBuilder.append(' ');

        previousPartEnd += delta;
        if (previousPartEnd > 1) {
            stringBuilder.append(passwordRange.subdivide(1));
            previousPartEnd = 0;
        } else {
            stringBuilder.append(passwordRange.subdivide(previousPartEnd));
        }

        return stringBuilder.toString();
    }
}
