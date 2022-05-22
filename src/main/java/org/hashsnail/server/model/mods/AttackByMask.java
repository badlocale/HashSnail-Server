package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.PasswordRange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class AttackByMask extends AttackMode {
    private float previousPartEnd = 0;
    private final PasswordRange passwordRange;

    public AttackByMask(String rawMask) {
        super(ModesIdentifiers.MASK_ATTACK.ordinal());

        this.passwordRange = new PasswordRange(rawMask.toCharArray());
    }

    @Override
    public void writeNextRange(OutputStream out, double entireBenchmark, double personalBenchmark) throws IOException {
        double proportion = entireBenchmark / personalBenchmark;

        out.write(nextPasswordRange(proportion).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return super.modeValue + " " + passwordRange.toString();
    }

    private String nextPasswordRange(double delta) {
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
