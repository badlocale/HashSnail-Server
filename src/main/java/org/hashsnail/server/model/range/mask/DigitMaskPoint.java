package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabets;

public final class DigitMaskPoint extends MaskPoint {
    public DigitMaskPoint() {
        super('%', 'D', Alphabets.getDigit());
    }
}
