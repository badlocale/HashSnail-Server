package org.hashsnail.server.model;

public class Algorithm {
    private String name;
    private int codeNumber;
    private int bitLength;

    public Algorithm(String name, int codeNumber, int bitLength) {
        this.name = name;
        this.codeNumber = codeNumber;
        this.bitLength = bitLength;
    }

    public int getBitLength() {
        return bitLength;
    }

    public int getCodeNumber() {
        return codeNumber;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isValidHash(String strHash) {
        return false;
        //todo
    }
}
