package org.hashsnail.server;

import org.hashsnail.server.cli.CommandHandler;
import org.hashsnail.server.net.ConnectionHandler;
import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.*;
import org.apache.commons.cli.*;
import org.hashsnail.server.net.ServerSession;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;


public class Server {
    private static final List<ServerSession> sessions = Collections.synchronizedList(new ArrayList<ServerSession>());
    private static final List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<Socket>());
    private static final Map<String, String> results = Collections.synchronizedMap(new HashMap<String, String>());
    private static Thread listenerThread = null;
    private static AttackMode attackMode = null;
    private static Algorithm algorithm = new Algorithm("MD5", 0, 16);
    private static Path hashFilePath = Paths.get("hash.txt");
    private static String singleHash = null;
    private static double entireBenchmarkWork = 0;

    public static void main(String[] args) {
        int port = 8000;

        //*** Creating a cmd parser ***
        Options cliOptions = new Options();

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
            port = Integer.parseInt(cmd.getOptionValue(notDefaultPortOption));
        }

        if (cmd.hasOption((algorithmToAttackOption))) {
            String algoritmName = cmd.getOptionValue(algorithmToAttackOption);

            algorithm = switch (algoritmName) {
                case "SHA1" -> new Algorithm("SHA1", 1, 20);
                default -> new Algorithm("MD5", 0, 16);
            };
        }

        if (cmd.hasOption(isMultipleHashOption)) {
            if (cmd.getOptionValue(isMultipleHashOption) != null) {
                hashFilePath = Paths.get(cmd.getOptionValue(isMultipleHashOption));
            }

            if (!Files.isReadable(hashFilePath)) {
                throw new InvalidPathException(hashFilePath.toString(), "Not readable file format.");
            }
        }

        if (cmd.hasOption(isSingleHashOption)) {
            String hash = cmd.getOptionValue(isSingleHashOption);

            if (algorithm.isValidHash(hash)) {
                singleHash = hash;
            } else {
                System.err.println("Not correct hash value.");
            }
        }

        if (!cmd.hasOption(isMultipleHashOption) && !cmd.hasOption(isSingleHashOption)) {
            System.out.println("Please enter the hash value.");
            Scanner s = new Scanner(System.in);
            singleHash = s.nextLine();
            s.reset();
            s.close();
        }

        boolean isAttackModeChosen = false;
        if (cmd.hasOption(bruteforceAttackOption)) {
            int numberOfElements = Integer.parseInt(cmd.getOptionValue(bruteforceAttackOption));
            attackMode = new ClassicBruteforce(numberOfElements);
            isAttackModeChosen = true;
        }

        if (cmd.hasOption(maskAttackOption)) {
            String mask = cmd.getOptionValue(maskAttackOption);
            attackMode = new AttackByMask(mask);
            isAttackModeChosen = true;
        }

        if (cmd.hasOption(dictionaryAttackOption)) {
            Path dictionaryPath;

            if (cmd.getOptionValue(dictionaryAttackOption) != null) {
                dictionaryPath = Paths.get(cmd.getOptionValue(dictionaryAttackOption));

                if (!Files.isReadable(dictionaryPath)) {
                    System.err.println("Not correct dictionary path.");
                    System.exit(-1);
                }
            } else {
                dictionaryPath = Paths.get("dictionary.txt");
            }

            attackMode = new AttackByDictionary(dictionaryPath);
            isAttackModeChosen = true;
        }

        if (!isAttackModeChosen) {
            System.out.println("Enter the max password length for \"brute force\" attack.");
            Scanner s = new Scanner(System.in);
            attackMode = new ClassicBruteforce(s.nextInt());
            s.reset();
        }

        //*** start listening ***
        startListening(port);

        //*** handle commands ***
        CommandHandler commandHandler = new CommandHandler(System.in);
        while(true) {
            commandHandler.handleCommand();
        }
    }

    public static void startListening(int port) {
        ConnectionHandler connectionHandler = new ConnectionHandler(port, clientSockets);
        listenerThread = new Thread(connectionHandler);
        listenerThread.start();
    }

    public static void startSessions() {
        listenerThread.interrupt();
        ExecutorService calculationsThreadPool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < clientSockets.size(); i++) {
            sessions.add(new ServerSession(clientSockets.get(i)));
            calculationsThreadPool.submit(sessions.get(i));
        }
    }

    public static List<ServerSession> getAllSessions() {
        return sessions;
    }

    public static boolean isEveryoneReady() {
        if (sessions.isEmpty()) {
            return false;
        }

        for (ServerSession session: sessions) {
            if (!session.isReady()) {
                return false;
            }
        }
        return true;
    }

    public static Algorithm getAlgorithm() {
        return algorithm;
    }

    public static AttackMode getAttackMode() {
        return attackMode;
    }

    public static Path getHashFilePath() {
        return hashFilePath;
    }

    public static String getSingleHash() {
        return singleHash;
    }

    public static double getEntireBenchmarkWork() {
        return entireBenchmarkWork;
    }

    public static void appendBenchmarkResult(double benchResult) {
        entireBenchmarkWork += benchResult;
    }

    public static void appendResult(String password, String hash) {
        results.put(password, hash);
    }
}
