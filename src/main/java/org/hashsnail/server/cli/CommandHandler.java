package org.hashsnail.server.cli;

import java.io.*;
import java.util.Scanner;

public final class CommandHandler {
    private InputStream inputStream;

    public CommandHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void waitCommand() {
        Scanner scanner = new Scanner(inputStream);
        String[] line = scanner.nextLine().split(" ");
        //todo Возможно багует на пустых строках, проверить

        if (line[0].startsWith("/")) {
            String commandName = line[0].substring(1);

            String[] params = new String[line.length - 1];
            System.arraycopy(line, 1, params, 0, params.length);

            switch (commandName) {
                case ("start"):
                    System.out.println("start"); //todo
                    break;
                case ("exit"):
                    System.out.println("exit");
                    break;
                case ("benchmark"):
                    System.out.println("benchmark");
                    break;
                case ("help"):
                    System.out.println("help");
                default:
                    break;
            }
        }
    }
}