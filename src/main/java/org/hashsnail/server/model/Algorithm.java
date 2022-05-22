package org.hashsnail.server.model;

public class Algorithm {
    private String name;
    private int codeNumber;
    private int byteLength;

    public Algorithm(String name, int codeNumber, int byteLength) {
        this.name = name;
        this.codeNumber = codeNumber;
        this.byteLength = byteLength;
    }

    public int getHashByteLength() {
        return byteLength;
    }

    public int getCodeNumber() {
        return codeNumber;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isValidHash(String strHash) {
        return true;
        //todo
    }
}
