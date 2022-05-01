package org.hashsnail.server.cli;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public final class CommandHandler {
    private InputStream inputStream;
    private ArrayList<Command> commands;

    public CommandHandler(InputStream inputStream, ArrayList<Command> commands) {
        this.inputStream = inputStream;
        this.commands = commands;
    }

    public void waitCommand() {
        Scanner scanner = new Scanner(inputStream);
        while (true) {
            String[] line = scanner.nextLine().split(" ");

            if (line[0].startsWith("/")) {
                String commandName = line[0].substring(1);
                String[] params = new String[line.length - 1];
                System.arraycopy(line, 1, params, 0, params.length);
                for (Command command: commands) {
                    if (command.getName().equals(commandName)) {
                        command.execute(params);
                    }
                }
            }
        }
    }
}