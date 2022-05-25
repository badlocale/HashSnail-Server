package org.hashsnail.server.model;

public class Algorithm {
    private final String name;
    private final int codeNumber;
    private final int byteLength;

    public Algorithm(String name, int codeNumber, int byteLength) {
        this.name = name;
        this.codeNumber = codeNumber;
        this.byteLength = byteLength;
    }

    public int getCodeNumber() {
        return codeNumber;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    public boolean isValidHashes(String ... hashes) {
        String pattern = "\\w{" + byteLength + "}$";

        for (String hash : hashes) {
            if (!hash.matches(pattern)) {
                return false;
            }
        }

        return true;
    }
}
