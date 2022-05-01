package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabets;

public final class LowerMaskPoint extends MaskPoint {
    public LowerMaskPoint() {
        super('%', 'L', Alphabets.getLower());
    }
}
