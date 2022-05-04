package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.Alphabets;
import org.hashsnail.server.model.range.MaskPoint;
import org.hashsnail.server.model.range.PasswordRange;

public final class ClassicBruteforce extends AttackMode {
    PasswordRange passwordRange;
    int maxNumberOfElements;

    public ClassicBruteforce(int elementsNumber) throws IllegalArgumentException{
        if (elementsNumber < 0) {
            throw new IllegalArgumentException("N must be more than 0. There is no sequence of zero elements");
        }
        this.passwordRange = new PasswordRange(new MaskPoint('%', 'F', Alphabets.getFull()), elementsNumber);
        this.maxNumberOfElements = elementsNumber;
    }
}
