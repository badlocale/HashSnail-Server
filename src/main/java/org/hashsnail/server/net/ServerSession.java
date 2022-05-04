package org.hashsnail.server.net;

import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.AttackMode;

import java.io.*;
import java.net.Socket;

public class ServerSession implements Runnable {
    private Socket socket = null;
    private AttackMode attackMode = null;
    private Algorithm algorithm = null;
    private double benchmarkResult = -1;
    private Boolean isReadyToBenchmark = false;
    private Boolean isReadyToCalculate = false;

    public ServerSession(Socket socket, AttackMode attackMode, Algorithm algorithm) {
        this.socket = socket;
        this.attackMode = attackMode;
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        synchronized (isReadyToBenchmark) {
            try {
                while (!isReadyToBenchmark) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Session for address" + socket.getInetAddress() + "is interrupted.");
            }
        }

        try {
            requestBenchmark(10);
        } catch (IOException e) {
            System.err.println("Cant send benchmark request for client by address " + socket.getInetAddress() + ".");
        }

        synchronized (isReadyToCalculate) {
            try {
                while (!isReadyToCalculate) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Session for address" + socket.getInetAddress() + "is interrupted.");
            }
        }

        requestWorkData();
    }

    public synchronized void startBenchmark() {
        isReadyToBenchmark = true;
        synchronized (isReadyToBenchmark) {
            notifyAll();
        }
    }

    public synchronized void startCalculate() {
        isReadyToCalculate = true;
        synchronized (isReadyToBenchmark) {
            notifyAll();
        }
    }

    private double requestBenchmark(int sec) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String[] clientsResponse = null;
        double benchResult = 0;

        while (benchResult <= 0) {
            writer.write("benchmark:" + sec + algorithm.toString());
            writer.flush();

            try {
                clientsResponse = reader.readLine().split(":");

                if (clientsResponse[0] == "bench-result" && clientsResponse.length > 1) {
                    benchResult = Double.parseDouble(clientsResponse[1]);
                }
            } catch (Exception e) {
                System.err.println("Client by address " + socket.getInetAddress() +
                                   "returned not correct benchmark result. Request will be duplicated.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return benchResult;
    }

    private void requestWorkData() {
        System.out.println("FFF");
    }
}
