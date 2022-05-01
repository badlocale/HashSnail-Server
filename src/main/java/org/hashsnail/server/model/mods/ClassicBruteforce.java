package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.PasswordRange;
import org.hashsnail.server.model.range.mask.FullMaskPoint;
import org.hashsnail.server.model.range.mask.MaskPoint;

public final class ClassicBruteforce extends AttackMode {
    PasswordRange passwordRange;
    int maxNumberOfElements;

    public ClassicBruteforce(int elementsNumber) throws IllegalArgumentException{
        if (elementsNumber < 0) {
            throw new IllegalArgumentException("N must be more than 0. There is no sequence of zero elements");
        }
        this.passwordRange = new PasswordRange(new FullMaskPoint(), elementsNumber);
        this.maxNumberOfElements = elementsNumber;
    }
}
