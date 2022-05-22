package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.Alphabets;
import org.hashsnail.server.model.range.MaskPoint;
import org.hashsnail.server.model.range.PasswordRange;

import java.io.IOException;
import java.io.OutputStream;

public final class ClassicBruteforce extends AttackMode {
    private final PasswordRange passwordRange;
    private final int maxNumberOfElements;
    private float previousPartEnd = 0;

    public ClassicBruteforce(int elementsNumber) throws IllegalArgumentException {
        super(ModesIdentifiers.CLASSIC_BRUTEFORCE.ordinal());

        if (elementsNumber < 0) {
            throw new IllegalArgumentException("N must be more than 0. There is no sequence of zero elements");
        }
        this.passwordRange = new PasswordRange(new MaskPoint('%', 'F', Alphabets.getFull()), elementsNumber);
        this.maxNumberOfElements = elementsNumber;
    }

    @Override
    public void writeNextRange(OutputStream out, double entireBenchmark, double personalBenchmark) throws IOException {

    }
}
