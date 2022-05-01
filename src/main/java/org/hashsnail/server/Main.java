package org.hashsnail.server;

import org.hashsnail.server.net.ConnectionHandler;
import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.*;
import org.apache.commons.cli.*;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;


public class Main  {
    private static List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<Socket>());
    private static AttackMode attackMode = null;
    private static Algorithm algoritm = new Algorithm("MD5", 0, 128);
    private static Path hashFilePath = Paths.get("hash.txt");
    private static String singleHash = null;

    public static void main(String[] args) throws Exception {
        Integer port = 8000;

        //*** Creating a cmd parser ***
        Options cliOptions = new Options();  //todo добавить option ко всем опциям

        Option notDefaultPort = new Option("p", "port", true, "port");
        notDefaultPort.setArgs(1);
        notDefaultPort.setOptionalArg(true);
        notDefaultPort.setArgName("port for listening");

        Option algorithmToAttack = new Option("a", "algoritm", true, "hash algoritm");
        algorithmToAttack.setArgs(1);
        algorithmToAttack.setOptionalArg(false);
        algorithmToAttack.setArgName("hash algoritm naming");

        Option bruteforceAttack = new Option("b", "brute-force", true, "use brute-force");
        bruteforceAttack.setArgs(1);
        bruteforceAttack.setOptionalArg(false);
        bruteforceAttack.setArgName("word length");

        Option maskAttack = new Option("m", "mask", true, "use attack by mask");
        maskAttack.setArgs(1);
        maskAttack.setOptionalArg(false);
        maskAttack.setArgName("mask");

        Option dictionaryAttack = new Option("d", "dictionary", true, "use dictionary attack");
        dictionaryAttack.setArgs(1);
        dictionaryAttack.setOptionalArg(true);
        dictionaryAttack.setArgName("dictionary directory");

        OptionGroup attackModsGroup = new OptionGroup();
        attackModsGroup.addOption(bruteforceAttack);
        attackModsGroup.addOption(maskAttack);
        attackModsGroup.addOption(dictionaryAttack);

        Option isSingleHash = new Option("s", "single", true, "take only one hash");
        isSingleHash.setArgs(1);
        isSingleHash.setOptionalArg(false);
        isSingleHash.setArgName("hash");

        Option isMultipleHash = new Option("h", "hash-set", true, "take a set of hashes");
        isMultipleHash.setArgs(1);
        isMultipleHash.setOptionalArg(true);
        isMultipleHash.setArgName("directory path");

        OptionGroup hashNumberGroup = new OptionGroup();
        hashNumberGroup.addOption(isSingleHash);
        hashNumberGroup.addOption(isMultipleHash);

        cliOptions.addOption(notDefaultPort);
        cliOptions.addOption(algorithmToAttack);
        cliOptions.addOptionGroup(attackModsGroup);
        cliOptions.addOptionGroup(hashNumberGroup);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        cmd = parser.parse(cliOptions, args);

        //*** Handle options ***
        if (cmd.hasOption(notDefaultPort)) {
            port = Integer.valueOf(cmd.getOptionValue(notDefaultPort));
        }

        if (cmd.hasOption((algorithmToAttack))) {
            String algoritmName = cmd.getOptionValue(algorithmToAttack);
            Algorithm chosenAlgoritm = switch (algoritmName) {
                case "MD5" -> new Algorithm("MD5", 0, 128);
                case "SHA1" -> new Algorithm("SHA1", 1, 160);
                default -> null;
            };

            if (chosenAlgoritm != null) {
                algoritm = chosenAlgoritm;
            }
        }

        if (cmd.hasOption(isMultipleHash)) {
            if (cmd.getOptionValue(isMultipleHash) != null) {
                hashFilePath = Paths.get(cmd.getOptionValue(isMultipleHash));
            }

            if (!hashFilePath.toString().endsWith("txt")) {
                throw new InvalidPathException(hashFilePath.toString(), "Not readable file format");
            }
        }

        if (cmd.hasOption(isSingleHash)) {
            singleHash = cmd.getOptionValue(isSingleHash);//todo Проверка валидности строки
                                                          //algoritm.checkValid(singleHash);a
        }

        if (!cmd.hasOption(isMultipleHash) && !cmd.hasOption(isSingleHash)) {
            System.out.println("Please enter the hash value");
            Scanner s = new Scanner(System.in);
            singleHash = s.nextLine();
        }

        if (cmd.hasOption(bruteforceAttack)) {
            int numberOfElements = Integer.valueOf(cmd.getOptionValue(bruteforceAttack)).intValue();
            attackMode = new ClassicBruteforce(numberOfElements);
        }

        if (cmd.hasOption(maskAttack)) {
            attackMode = new AttackByMask(cmd.getOptionValue(maskAttack));

        }

        if (cmd.hasOption(dictionaryAttack)) {
            attackMode = new AttackByDictionary(cmd.getOptionValue(dictionaryAttack));
        }

        //*** start listening ***
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ConnectionHandler listenerThread = new ConnectionHandler(port, clientSockets);
        Future future = executor.submit(listenerThread);
        System.out.println("Port " + listenerThread.getPort() + " is listening now");
    }
}
