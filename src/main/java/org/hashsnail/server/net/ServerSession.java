package org.hashsnail.server.net;

import org.hashsnail.server.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ServerSession implements Runnable {
    private static final char POCKET_END = (char) 10;
    private static final char INTERNAL_DATA_SEPARATOR = (char) 32;
    private final Socket socket;
    private double benchmarkResult = -1;
    private Boolean isReadyToCalculate = false;

    public ServerSession(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            benchmarkResult = requestBenchmark(10);
        } catch (IOException e) {
            System.err.println("[Connections] Cant send benchmark request or receive benchmark " +
                    "response for client by address " +
                    socket.getInetAddress() + ".");
        }

        isReadyToCalculate = true;
        Server.appendBenchmarkResult(benchmarkResult);

        synchronized (ServerSession.class) {
            ServerSession.class.notifyAll();

            try {
                while (!Server.isEveryoneReady()) {
                    ServerSession.class.wait();
                }
            } catch (InterruptedException e) {
                System.err.println("[Connections] Session for address" + socket.getInetAddress() + "is interrupted.");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(Thread.currentThread().getName() + "complete benchmark"); //todo убрать

        try {
            requestWorkData();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();//todo
        }
    }

    private double requestBenchmark(int sec) throws IOException {
        OutputStream out = socket.getOutputStream();
        String request = ((byte)PocketType.BENCHMARK_REQUEST.ordinal() + " " + sec);
        double benchResult = 0;
        String[] response;

        out.write(request.getBytes(StandardCharsets.UTF_8));
        out.flush();

        response = readPocket();
        if (Integer.parseInt(response[0]) == PocketType.BENCHMARK_RESULT.ordinal()
                && Float.parseFloat(response[1]) > 0) {
            benchResult = Double.parseDouble(response[1]);
        }

        return benchResult;
    }

    private void requestWorkData() throws IOException, InterruptedException {
        String[] response;

        sendHash();
        sendRange();

        response = readPocket();
        if (Byte.parseByte(response[0]) == PocketType.RESULTS.ordinal()) {
            for (int i = 1; i < response.length; i += 2) {
                Server.appendResult(response[i], response[i + 1]);
            }
        }
    }

    private void sendHash() throws IOException {
        OutputStream out = socket.getOutputStream();
        String header;
        String hashData;

        header = (PocketType.HASH_DATA.ordinal() + " ");

        if (Server.getSingleHash() != null) {
            hashData = Server.getSingleHash();
        } else {
            try (InputStream fileInputStream = Files.newInputStream(Server.getHashFilePath())) {
                StringBuilder stringBuilder = new StringBuilder();
                int i = -1;

                while ((i = fileInputStream.read()) != -1) {
                    stringBuilder.append((char) i);
                }

                hashData = stringBuilder.toString().replace('\n', ' ');
            }
        }

        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(hashData.getBytes(StandardCharsets.UTF_8));
        out.write(POCKET_END);
        out.flush();
    }

    private void sendRange() throws IOException {
        OutputStream out = socket.getOutputStream();
        String header;

        header = (PocketType.RANGE_DATA.ordinal() + " " +
                Server.getAttackMode().toString() + " " +
                Server.getAlgorithm().toString() + " ");

        out.write(header.getBytes(StandardCharsets.UTF_8));
        Server.getAttackMode().writeNextRange(out ,benchmarkResult, Server.getEntireBenchmarkWork());

        out.write(POCKET_END);
        out.flush();
    }

//    private void sendStringData(OutputStream out, String ... data) throws IOException {
//        for (String dataPart: data) {
//            out.write(dataPart.getBytes(StandardCharsets.UTF_8));
//        }
//
//        out.write(POCKET_END);
//        out.flush();
//    }

    private String[] readPocket() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        byte i = -1;

        while ((i = (byte)in.read()) != POCKET_END) {
            stringBuilder.append((char) i);
        }

        return stringBuilder.toString().split(String.valueOf(INTERNAL_DATA_SEPARATOR));
    }

    public boolean isReady() {
        return isReadyToCalculate;
    }
}