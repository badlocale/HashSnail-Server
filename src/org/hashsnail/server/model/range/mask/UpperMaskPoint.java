package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabet;

public final class UpperMaskPoint extends MaskPoint {
    public UpperMaskPoint() {
        super.precedingSymbol = '%';
        super.symbol = 'U';
        super.alphabet = Alphabet.getUpper();
    }
}
