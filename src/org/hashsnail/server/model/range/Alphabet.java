package org.hashsnail.server.model.range;

public abstract class Alphabet {
    static final private String fullAlphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final private String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
    static final private String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final private String digitAlphabet = "0123456789";

    public static char[] getFull() {
        return fullAlphabet.toCharArray();
    }

    public static char[] getDigit() {
        return digitAlphabet.toCharArray();
    }

    public static char[] getLower() {
        return lowerAlphabet.toCharArray();
    }

    public static char[] getUpper() {
        return upperAlphabet.toCharArray();
    }
}
