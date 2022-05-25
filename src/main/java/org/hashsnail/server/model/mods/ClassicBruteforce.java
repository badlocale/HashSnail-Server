package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.Alphabets;
import org.hashsnail.server.model.range.MaskPoint;
import org.hashsnail.server.model.range.PasswordRange;
import org.hashsnail.server.net.PocketTypes;
import org.hashsnail.server.net.PocketWriter;

public final class ClassicBruteforce extends AttackMode {
    private final PasswordRange passwordRange;
    private final int maxNumberOfElements;
    private float previousPartEnd = 0;

    public ClassicBruteforce(int elementsNumber) throws IllegalArgumentException {
        super(PocketTypes.DICTIONARY_DATA.ordinal());

        if (elementsNumber < 0) {
            throw new IllegalArgumentException("N must be more than 0. There is no sequence of zero elements");
        }
        this.passwordRange = new PasswordRange(new MaskPoint('%', 'F', Alphabets.getFull()), elementsNumber);
        this.maxNumberOfElements = elementsNumber;
    }

    public void writeDataAsPocket(PocketWriter writer, String header, double entireBenchmark, double personalBenchmark) {

    }

}
