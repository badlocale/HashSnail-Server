package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabets;

public final class UpperMaskPoint extends MaskPoint {
    public UpperMaskPoint() {
        super('%', 'U', Alphabets.getUpper());
    }
}
