package org.hashsnail.server.net;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PocketWriter {
    private final int CLIENTS_BUFFER_SIZE;
    private final char INTERNAL_SEPARATOR;
    private final char POCKET_END;
    private final OutputStream out;

    public PocketWriter(OutputStream out, int clientsBufferSize, char internalSeparator, char pocketEnd) {
        this.out = out;
        this.CLIENTS_BUFFER_SIZE = clientsBufferSize;
        this.INTERNAL_SEPARATOR = internalSeparator;
        this.POCKET_END = pocketEnd;
    }

    synchronized public long writeDataFromFile(String header, Path dataPath, long start, long maximumBytes) {
        long bytesWritten = 0;

        try (InputStream in = Files.newInputStream(dataPath)) {
            StringBuilder stringBuilder = new StringBuilder();
            int lastSeparatorIndex = 0;
            int i;
            boolean isFileEnd = false;

            in.skip(start);

            while (bytesWritten != maximumBytes) {
                i = in.read();

                if (i == '\n' || i == '\r') {
                    i = INTERNAL_SEPARATOR;
                } else if (i == -1) {
                    isFileEnd = true;
                }

                stringBuilder.append((char) i);

                if (i == INTERNAL_SEPARATOR || isFileEnd) {
                    if (isFileEnd) {
                        lastSeparatorIndex = stringBuilder.length() - 1;
                    }

                    if (stringBuilder.length() > CLIENTS_BUFFER_SIZE || isFileEnd) {
                        out.write(header.getBytes(StandardCharsets.UTF_8));

                        out.write(INTERNAL_SEPARATOR);

                        String data = stringBuilder.substring(0, lastSeparatorIndex);
                        System.out.println(bytesWritten + " " + stringBuilder);//
                        bytesWritten += data.length();
                        out.write(data.getBytes(StandardCharsets.UTF_8));

                        out.write(POCKET_END);
                        out.flush();
                        Thread.sleep(200);

                        stringBuilder.delete(0, lastSeparatorIndex);
                    }
                    lastSeparatorIndex = stringBuilder.length() - 1;
                }

                if (i == -1) {
                    break;
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return bytesWritten;
    }

    public void writeData(String ... data) throws IOException {
        for (String dataPart : data) {
            out.write(dataPart.getBytes(StandardCharsets.UTF_8));
            out.write(INTERNAL_SEPARATOR);
        }
        out.write(POCKET_END);
        out.flush();
    }
}
