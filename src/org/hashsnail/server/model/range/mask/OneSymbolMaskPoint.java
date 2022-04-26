package org.hashsnail.server.model.range.mask;

public class OneSymbolMaskPoint extends MaskPoint {
    public OneSymbolMaskPoint(char symbol) {
        super.precedingSymbol = '#';
        super.symbol = symbol;
        super.alphabet = new char[] { symbol };
    }
}
