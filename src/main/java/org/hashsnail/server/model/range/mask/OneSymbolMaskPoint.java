package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabets;

public final class OneSymbolMaskPoint extends MaskPoint {
    public OneSymbolMaskPoint(char symbol) {
        super('#', symbol, new char[] { symbol });
    }
}
