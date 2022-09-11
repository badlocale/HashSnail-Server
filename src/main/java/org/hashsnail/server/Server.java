package org.hashsnail.server;

import org.hashsnail.server.cli.CommandHandler;
import org.hashsnail.server.cli.ArgumentsParser;
import org.hashsnail.server.net.ConnectionHandler;
import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.AttackMode;
import org.hashsnail.server.model.mods.ClassicBruteforce;
import org.hashsnail.server.net.ServerSession;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final List<ServerSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private static final List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, String> results = Collections.synchronizedMap(new HashMap<>());
    private static Thread listenerThread = null;
    private static AttackMode attackMode = null;
    private static Algorithm algorithm = null;
    private static Path hashFilePath = null;
    private static String singleHash = null;
    private static double entireBenchmarkWork = 0;

    public static void main(String[] args) {

        //*** parsing arguments ***
        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        int port = argumentsParser.parsePort();

        algorithm = argumentsParser.parseAlgorithm();
        if (algorithm == null) {
            System.out.println("[Server-initializer] Hashing algorithm was not specified, MD5 will be used.");
            algorithm = new Algorithm("MD5", 0, 32);
        }

        hashFilePath = argumentsParser.parseHashFilePath();
        singleHash = argumentsParser.parseSingleHash();
        if (hashFilePath == null && singleHash == null) {
            hashFilePath = Paths.get("hash.txt");

            if (!Files.isReadable(hashFilePath)) {
                throw new IllegalArgumentException("[Server-initializer] Cant read \"hash.txt\" file.");
            }
        }

        attackMode = argumentsParser.parseAttackMode();
        if (attackMode == null) {
            System.out.println("[Server-initializer] Enter the max password length for \"brute-force\" attack.");
            Scanner s = new Scanner(System.in);
            attackMode = new ClassicBruteforce(s.nextInt());
            s.reset();
        }

        //*** start listening ***
        ConnectionHandler connectionHandler = new ConnectionHandler(port, clientSockets);
        listenerThread = new Thread(connectionHandler);
        listenerThread.start();

        //*** handle commands ***
        CommandHandler commandHandler = new CommandHandler(System.in);
        while(true) {
            commandHandler.handleCommand();
        }
    }

    public static void startSessions() {
        listenerThread.interrupt();
        ExecutorService calculationsThreadPool = Executors.newFixedThreadPool(20);
        try{
            for (int i = 0; i < clientSockets.size(); i++) {
                sessions.add(new ServerSession(clientSockets.get(i)));
                calculationsThreadPool.submit(sessions.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isEveryoneFinishedBenchmark() {
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

    public static void appendBenchmarkResult(double benchResult) {
        entireBenchmarkWork += benchResult;
    }

    public static void appendCalculatedPasswords(String password, String hash) {
        results.put(password, hash);
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
}
