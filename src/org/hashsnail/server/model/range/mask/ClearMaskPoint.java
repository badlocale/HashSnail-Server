package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabet;

public class ClearMaskPoint extends MaskPoint {
    public ClearMaskPoint() {
        super.precedingSymbol = '%';
        super.symbol = '*';
        super.alphabet = Alphabet.getFull();
    }
}
