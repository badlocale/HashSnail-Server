package org.hashsnail.server.model.range;

public class URange {
    private final MaskPoint[] mask;

    public URange(MaskPoint maskPoint, int length) throws IllegalArgumentException {
        if (length < 1)
            throw  new IllegalArgumentException("Password cant have less than one symbol.");
        mask = new MaskPoint[length];
        for (int i = 0; i < length; i++) {
            mask[i] = new MaskPoint(maskPoint);
        }
    }

    public char[] subdivide(double proportion) throws IllegalArgumentException {
        if (proportion <= 0) {
            return getFirstPassword();
        }

        if (proportion >= 1) {
            return getLastPassword();
        }

        char[] edgePassword = new char[mask.length];
        long[] base = new long[mask.length];
        long decimalRepresentation = 1;
        long symbolIndex;

        base[mask.length - 1] = 1;
        for (int i = mask.length - 2; i >= 0; i--) {
            base[i] = mask[i + 1].getAlphabet().length * base[i + 1];
        }
        decimalRepresentation = base[0] * mask[0].getAlphabet().length;

        decimalRepresentation *= proportion;

        for(int i = 0; i < mask.length; i++) {
            symbolIndex = (decimalRepresentation / base[i]);
            decimalRepresentation %= base[i];
            edgePassword[i] = mask[i].getAlphabet()[(int) symbolIndex];
        }

        return edgePassword;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mask.length; i++) {
            sb.append(mask[i].toString());
        }
        return  sb.toString();
    }

    public char[] getLastPassword() {
        char[] endSequence = new char[mask.length];
        for (int i = 0; i < mask.length; i++) {
            endSequence[i] = mask[i].getAlphabet()[mask[i].getAlphabet().length - 1];
        }
        return endSequence;
    }

    public char[] getFirstPassword() {
        char[] startSequence = new char[mask.length];
        for (int i = 0; i < mask.length; i++) {
            startSequence[i] = mask[i].getAlphabet()[0];
        }
        return startSequence;
    }
}
