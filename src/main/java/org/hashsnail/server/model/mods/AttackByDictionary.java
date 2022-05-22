package org.hashsnail.server.model.mods;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public final class AttackByDictionary extends AttackMode {
    private final Path dictionaryPath;

    public AttackByDictionary(Path dictionaryPath) {
        super(ModesIdentifiers.DICTIONARY_ATTACK.ordinal());

        this.dictionaryPath = dictionaryPath;
    }

    @Override
    public void writeNextRange(OutputStream out, double entireBenchmark, double personalBenchmark) throws IOException {

    }
}
