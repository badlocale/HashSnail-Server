package org.hashsnail.server.model.range.mask;

import org.hashsnail.server.model.range.Alphabets;

public final class FullMaskPoint extends MaskPoint {
    public FullMaskPoint() {
        super('%', 'F', Alphabets.getFull());
    }
}
