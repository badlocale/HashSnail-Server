package org.hashsnail.server.model.range.mask;

public abstract class MaskPoint {
    protected char precedingSymbol;
    protected char symbol;
    protected char[] alphabet;

    public final char[] getAlphabet() {
        return  alphabet;
    }

    @Override
    public final String toString() {
        return String.valueOf(precedingSymbol) + String.valueOf(symbol);
    }
}

