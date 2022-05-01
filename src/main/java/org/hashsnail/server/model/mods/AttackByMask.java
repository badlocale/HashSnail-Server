package org.hashsnail.server.model.mods;

import org.hashsnail.server.model.range.PasswordRange;

public final class AttackByMask extends AttackMode {
    PasswordRange passwordRange;

    public AttackByMask(String rawMask) {
        this.passwordRange = new PasswordRange(rawMask.toCharArray());
    }
}
