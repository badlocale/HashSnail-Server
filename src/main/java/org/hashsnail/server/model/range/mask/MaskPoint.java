package org.hashsnail.server.model.range.mask;

public class MaskPoint {
    protected char precedingSymbol;
    protected char symbol;
    protected char[] alphabet;

    public MaskPoint(char precedingSymbol, char symbol, char[] alphabet) {
        this.precedingSymbol = precedingSymbol;
        this.symbol = symbol;
        this.alphabet = alphabet.clone();
    }

    public MaskPoint(MaskPoint maskPoint) {
        this.precedingSymbol = maskPoint.precedingSymbol;
        this.symbol = maskPoint.symbol;
        this.alphabet = maskPoint.alphabet.clone();
    }

    public final char[] getAlphabet() {
        return  alphabet;
    }

    @Override
    public final String toString() {
        return String.valueOf(precedingSymbol) + String.valueOf(symbol);
    }
}

