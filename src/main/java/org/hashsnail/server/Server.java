package org.hashsnail.server;

import org.hashsnail.server.cli.CommandHandler;
import org.hashsnail.server.net.ConnectionHandler;
import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.*;
import org.apache.commons.cli.*;
import org.hashsnail.server.net.ServerSession;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;


public class Server {
    private static List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<Socket>());
    private static AttackMode attackMode = null;
    private static Algorithm algoritm = new Algorithm("MD5", 0, 128);
    private static Path hashFilePath = Paths.get("hash.txt");
    private static String singleHash = null;
    private static double allClientsBenchmarkWork = 0;

    public static void main(String[] args) {
        Integer port = 8000;

        //*** Creating a cmd parser ***
        Options cliOptions = new Options();  //todo добавить option ко всем опциям

        Option notDefaultPortOption = new Option("p", "port", true, "port");
        notDefaultPortOption.setArgs(1);
        notDefaultPortOption.setOptionalArg(true);
        notDefaultPortOption.setArgName("port for listening");

        Option algorithmToAttackOption = new Option("a", "algoritm", true, "hash algoritm");
        algorithmToAttackOption.setArgs(1);
        algorithmToAttackOption.setOptionalArg(false);
        algorithmToAttackOption.setArgName("hash algoritm naming");

        Option bruteforceAttackOption = new Option("b", "brute-force", true, "use brute-force");
        bruteforceAttackOption.setArgs(1);
        bruteforceAttackOption.setOptionalArg(false);
        bruteforceAttackOption.setArgName("word length");

        Option maskAttackOption = new Option("m", "mask", true, "use attack by mask");
        maskAttackOption.setArgs(1);
        maskAttackOption.setOptionalArg(false);
        maskAttackOption.setArgName("mask");

        Option dictionaryAttackOption = new Option("d", "dictionary", true, "use dictionary attack");
        dictionaryAttackOption.setArgs(1);
        dictionaryAttackOption.setOptionalArg(true);
        dictionaryAttackOption.setArgName("dictionary directory");

        OptionGroup attackModsGroup = new OptionGroup();
        attackModsGroup.addOption(bruteforceAttackOption);
        attackModsGroup.addOption(maskAttackOption);
        attackModsGroup.addOption(dictionaryAttackOption);

        Option isSingleHashOption = new Option("s", "single", true, "take only one hash");
        isSingleHashOption.setArgs(1);
        isSingleHashOption.setOptionalArg(false);
        isSingleHashOption.setArgName("hash");

        Option isMultipleHashOption = new Option("h", "hash-set", true, "take a set of hashes");
        isMultipleHashOption.setArgs(1);
        isMultipleHashOption.setOptionalArg(true);
        isMultipleHashOption.setArgName("directory path");

        OptionGroup hashNumberGroup = new OptionGroup();
        hashNumberGroup.addOption(isSingleHashOption);
        hashNumberGroup.addOption(isMultipleHashOption);

        cliOptions.addOption(notDefaultPortOption);
        cliOptions.addOption(algorithmToAttackOption);
        cliOptions.addOptionGroup(attackModsGroup);
        cliOptions.addOptionGroup(hashNumberGroup);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(cliOptions, args);
        } catch (ParseException e) {
            System.err.println("Cant read your arguments. You may be running the program incorrectly.");
        }

        //*** Handle cli options ***
        if (cmd.hasOption(notDefaultPortOption)) {
            port = Integer.valueOf(cmd.getOptionValue(notDefaultPortOption));
        }

        if (cmd.hasOption((algorithmToAttackOption))) {
            String algoritmName = cmd.getOptionValue(algorithmToAttackOption);
            Algorithm chosenAlgoritm = switch (algoritmName) {
                case "MD5" -> new Algorithm("MD5", 0, 128);
                case "SHA1" -> new Algorithm("SHA1", 1, 160);
                default -> null;
            };

            if (chosenAlgoritm != null) {
                algoritm = chosenAlgoritm;
            }
        }

        if (cmd.hasOption(isMultipleHashOption)) {
            if (cmd.getOptionValue(isMultipleHashOption) != null) {
                hashFilePath = Paths.get(cmd.getOptionValue(isMultipleHashOption));
            }

            if (!hashFilePath.toString().endsWith("txt")) {
                throw new InvalidPathException(hashFilePath.toString(), "Not readable file format."); //todo заменить на проверку наличия файла
            }
        }

        if (cmd.hasOption(isSingleHashOption)) {
            singleHash = cmd.getOptionValue(isSingleHashOption);//todo Проверка валидности строки
                                                                //algoritm.checkValid(singleHash);
        }

        if (!cmd.hasOption(isMultipleHashOption) && !cmd.hasOption(isSingleHashOption)) {
            System.out.println("Please enter the hash value.");
            Scanner s = new Scanner(System.in);
            singleHash = s.nextLine();
            s.reset();
        }

        boolean isAttackModeChosen = false;
        if (cmd.hasOption(bruteforceAttackOption)) {
            int numberOfElements = Integer.valueOf(cmd.getOptionValue(bruteforceAttackOption)).intValue();
            attackMode = new ClassicBruteforce(numberOfElements);
            isAttackModeChosen = true;
        }

        if (cmd.hasOption(maskAttackOption)) {
            attackMode = new AttackByMask(cmd.getOptionValue(maskAttackOption));
            isAttackModeChosen = true;
        }

        if (cmd.hasOption(dictionaryAttackOption)) {
            attackMode = new AttackByDictionary(cmd.getOptionValue(dictionaryAttackOption));
            isAttackModeChosen = true;
        }

        if (!isAttackModeChosen) {
            System.out.println("Enter the max password length for \"brute force\" attack.");
            while (true) {
                Scanner s = new Scanner(System.in);
                attackMode = new ClassicBruteforce(s.nextInt());
                s.reset();
                break;
            }
        }
        

        //*** start listening ***
        ExecutorService listenerThreadPool = Executors.newFixedThreadPool(1);
        ConnectionHandler listenerThread = new ConnectionHandler(port, clientSockets);
        Future future = listenerThreadPool.submit(listenerThread);


        ExecutorService calculationsThreadPool = Executors.newFixedThreadPool(10);
        ArrayList<ServerSession> sessions = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++) {
            sessions.add(new ServerSession(clientSockets.get(i), attackMode, algoritm));
            calculationsThreadPool.submit(sessions.get(i));
        }

        //*** handle commands ***
        CommandHandler commandHandler = new CommandHandler(System.in);
        while(true) {
            commandHandler.waitCommand();
        }
    }
}
