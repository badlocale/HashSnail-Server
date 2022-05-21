package org.hashsnail.server.cli;

import java.io.*;
import java.util.Scanner;

public final class CommandHandler {
    private InputStream inputStream;

    public CommandHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void handleCommand() {
        Scanner scanner = new Scanner(inputStream);
        String[] line = scanner.nextLine().split(" ");
        //todo Возможно багует на пустых строках, проверить

        if (line[0].startsWith("/")) {
            String commandName = line[0].substring(1);

            String[] params = new String[line.length - 1];
            System.arraycopy(line, 1, params, 0, params.length);

            Command command = switch (commandName) {
                case ("start") -> new StartCommand();
                case ("exit") -> new ExitCommand();
                default -> new UnidentifiedCommand();
            };

            command.execute();
        }
    }
}