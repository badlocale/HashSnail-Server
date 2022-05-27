package org.hashsnail.server.net;

import org.hashsnail.server.Server;

import java.io.*;
import java.net.Socket;

public class ServerSession implements Runnable {
    private static final char POCKET_END = (char) 10;
    private static final char INTERNAL_SEPARATOR = (char) 32;
    private static final int CLIENTS_BUFFER_SIZE = 50000;
    private final Socket socket;
    private final PocketWriter pocketWriter;
    private final PocketHandler pocketReader = null;
    private double benchmarkResult = -1;
    private Boolean isReadyToCalculate = false;

    public ServerSession(Socket socket) throws IOException {
        this.pocketWriter = new PocketWriter(socket.getOutputStream(), CLIENTS_BUFFER_SIZE,
                INTERNAL_SEPARATOR, POCKET_END);
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            benchmarkResult = requestBenchmark(5);
        } catch (IOException e) {
            System.err.println("[Connections] Cant send benchmark request or receive benchmark " +
                    "response for client by address " + socket.getInetAddress().getHostAddress() + ".");
        }

        isReadyToCalculate = true;
        Server.appendBenchmarkResult(benchmarkResult);

        synchronized (ServerSession.class) {
            ServerSession.class.notifyAll();

            try {
                while (!Server.isEveryoneFinishedBenchmark()) {
                    ServerSession.class.wait();
                }
            } catch (InterruptedException e) {
                System.err.println("[Connections] Session for address " + socket.getInetAddress().getHostAddress() +
                        "] is interrupted.");
                Thread.currentThread().interrupt();
            }

            System.out.println("[Connections] Client by address " + socket.getInetAddress().getHostAddress() +
                    " complete benchmark. Productivity: " + String.format("%.2f",benchmarkResult) + "MH/s");
        }

        try {
            requestWorkData();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();//todo обработрать
        }
    }

    private double requestBenchmark(int sec) throws IOException {
        double benchResult = 0;

        String request = ((byte) PocketTypes.BENCHMARK_REQUEST.ordinal() + " " + sec);
        pocketWriter.writeData(request);

        String[] response = readPocket();
        if (Integer.parseInt(response[0]) == PocketTypes.BENCHMARK_RESULT.ordinal()
                && Float.parseFloat(response[1]) > 0) {
            benchResult = Double.parseDouble(response[1]);
        }

        return benchResult;
    }

    private void requestWorkData() throws IOException, InterruptedException {
        String hashesHeader = (String.valueOf(PocketTypes.HASH_DATA.ordinal()));
        if (Server.getSingleHash() != null) {
            pocketWriter.writeData(hashesHeader ,Server.getSingleHash());
        } else {
            pocketWriter.writeDataFromFile(hashesHeader, Server.getHashFilePath(), 0, Long.MAX_VALUE);
        }

        String rangeHeader = Server.getAttackMode().toString();
        Server.getAttackMode().writeDataAsPocket(pocketWriter, rangeHeader,
                benchmarkResult, Server.getEntireBenchmarkWork());

        String[] response = readPocket();
        if (Byte.parseByte(response[0]) == PocketTypes.RESULTS.ordinal() && response[1] != null) {
            String time = response[1];

            System.out.println("[Connections] Received results from address " +
                    socket.getInetAddress().getHostAddress() + ". Calculation time: " + time);

            for (int i = 2; i < response.length; i += 2) {
                Server.appendCalculatedPasswords(response[i], response[i + 1]);
                System.out.println("              " + response[i] + " " + response[i + 1]);

            }
        } else {
            System.out.println("[Connections] Results from address " + socket.getInetAddress().getHostAddress() +
                    " received in wrong format");
        }
    }

    private String[] readPocket() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        byte i;

        while ((i = (byte)in.read()) != POCKET_END) {
            stringBuilder.append((char) i);
        }

        return stringBuilder.toString().split(String.valueOf(INTERNAL_SEPARATOR));
    }

    public boolean isReady() {
        return isReadyToCalculate;
    }
}