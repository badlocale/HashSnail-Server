package org.hashsnail.server.model.mods;

import org.hashsnail.server.Server;
import org.hashsnail.server.net.PocketTypes;
import org.hashsnail.server.net.PocketWriter;

import java.io.IOException;
import java.nio.file.Path;

public final class AttackByDictionary extends AttackMode {
    private final Path dictionaryPath;
    private final long fileSize;
    private volatile long totalBytesSent = 0;
    private long bytesNotSent = 0;

    public AttackByDictionary(Path dictionaryPath) {
        super(PocketTypes.DICTIONARY_DATA.ordinal());

        this.dictionaryPath = dictionaryPath;
        this.fileSize = dictionaryPath.toFile().length();
    }

    @Override
    public void writeDataAsPocket(PocketWriter writer, String header, double entireBenchmark, double personalBenchmark)
            throws IOException {
        double proportion = personalBenchmark / entireBenchmark;
        System.out.println("fileSize: " + fileSize);
        long dataSize = (long) (fileSize * proportion) + bytesNotSent;
        System.out.println("dataSize: " + dataSize);
        long bytesSent = writer.writeDataFromFile(String.valueOf(PocketTypes.DICTIONARY_DATA.ordinal()), dictionaryPath,
                totalBytesSent, dataSize);
        System.out.println("bytesSent: " + bytesSent);
        synchronized (this) {
            totalBytesSent += bytesSent;
        }
        System.out.println("totalBytesSent: " + totalBytesSent);
        bytesNotSent = dataSize - bytesSent;
        System.out.println("bytesNotSent: " + bytesNotSent);

        writer.writeData(String.valueOf(PocketTypes.DICTIONARY_START.ordinal()) ,
                String.valueOf(Server.getAlgorithm().getCodeNumber()));
    }
}
