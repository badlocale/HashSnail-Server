package org.hashsnail.server.model.range;

public class PasswordRange {
    private final MaskPoint[] mask;

    public PasswordRange(MaskPoint[] mask) throws IllegalArgumentException {
        if (mask.length > 0)
            this.mask = mask;
        else
            throw new IllegalArgumentException("Password cant have less than one symbol.");
    }

    public PasswordRange(MaskPoint maskPoint, int length) throws IllegalArgumentException {
        if (length < 1)
            throw  new IllegalArgumentException("Password cant have less than one symbol.");
        mask = new MaskPoint[length];
        for (int i = 0; i < length; i++) {
            mask[i] = new MaskPoint(maskPoint);
        }
    }

    public PasswordRange(char[] strMask) throws IllegalArgumentException {
        if (strMask.length % 2 == 1)
            throw new IllegalArgumentException("Password cant have less than one symbol.");

        mask = new MaskPoint[strMask.length / 2];
        for (int i = 0; i < strMask.length; i += 2) {
            if (strMask[i] == '%') {
                mask[i / 2] = switch (strMask[i + 1]) {
                    case 'L' -> new MaskPoint(strMask[i], strMask[i + 1], Alphabets.getLower());
                    case 'U' -> new MaskPoint(strMask[i], strMask[i + 1], Alphabets.getUpper());
                    case 'D' -> new MaskPoint(strMask[i], strMask[i + 1], Alphabets.getDigit());
                    case 'F' -> new MaskPoint(strMask[i], strMask[i + 1], Alphabets.getFull());
                    default -> null;
                };
            } else if (strMask[i] == '#') {
                mask[i / 2] = new MaskPoint(strMask[i], strMask[i + 1], new char[] { strMask[i + 1] });
            } else {
                mask[i / 2] = null;
            }
        }

        for (int i = 0; i < mask.length; i++) {
            if (mask[i] == null)
                throw new IllegalArgumentException("Cant identify symbol by number " + i  + " in mask.");
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
        int[] base = new int[mask.length];
        int decimalRepresentation = 1;
        int symbolIndex;

        base[mask.length - 1] = 1;
        for (int i = mask.length - 2; i >= 0; i--) {
            base[i] = mask[i + 1].getAlphabet().length * base[i + 1];
        }
        decimalRepresentation = base[0] * mask[0].getAlphabet().length;

        decimalRepresentation *= proportion;

        for(int i = 0; i < mask.length; i++) {
            symbolIndex = decimalRepresentation / base[i];
            decimalRepresentation %= base[i];
            edgePassword[i] = mask[i].getAlphabet()[symbolIndex];
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
