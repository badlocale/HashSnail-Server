package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabet;

public final class DigitMaskPoint extends MaskPoint {
    public DigitMaskPoint() {
        super.precedingSymbol = '%';
        super.symbol = 'D';
        super.alphabet = Alphabet.getDigit();
    }

}
