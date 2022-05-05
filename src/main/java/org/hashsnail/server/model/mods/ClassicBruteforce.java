package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.Alphabets;
import org.hashsnail.server.model.range.MaskPoint;
import org.hashsnail.server.model.range.PasswordRange;

public final class ClassicBruteforce extends AttackMode {
    private PasswordRange passwordRange;
    private int maxNumberOfElements;
    private float previousPartEnd = 0;

    public ClassicBruteforce(int elementsNumber) throws IllegalArgumentException{
        if (elementsNumber < 0) {
            throw new IllegalArgumentException("N must be more than 0. There is no sequence of zero elements");
        }
        this.passwordRange = new PasswordRange(new MaskPoint('%', 'F', Alphabets.getFull()), elementsNumber);
        this.maxNumberOfElements = elementsNumber;
    }

    public char[] nextPartOfWork(float delta) {
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

        return stringBuilder.toString().toCharArray();
    }
}
