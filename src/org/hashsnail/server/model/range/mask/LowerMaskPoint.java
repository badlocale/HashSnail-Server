package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabet;

public final class LowerMaskPoint extends MaskPoint {
    public LowerMaskPoint() {
        super.precedingSymbol = '%';
        super.symbol = 'L';
        super.alphabet = Alphabet.getLower();
    }
}
