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

    private final Object BenchMonitor = new Object();
    private final Object CalculateMonitor = new Object();
    private Boolean isReadyToBenchmark = false;
    private Boolean isReadyToCalculate = false;

    public ServerSession(Socket socket, AttackMode attackMode, Algorithm algorithm) {
        this.socket = socket;
        this.attackMode = attackMode;
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        synchronized (BenchMonitor) {
            try {
                while (!isReadyToBenchmark) {
                    BenchMonitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Session for address" + socket.getInetAddress() + "is interrupted.");
            }
        }

        try {
            benchmarkResult = requestBenchmark(10);
        } catch (IOException e) {
            System.err.println("Cant send benchmark request for client by address " + socket.getInetAddress() + ".");
        }

//        if (benchmarkResult > 0) {
//            isReadyToCalculate = true;
//        }

        synchronized (CalculateMonitor) {
            try {
                while (!isReadyToCalculate) {
                    CalculateMonitor.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Session for address" + socket.getInetAddress() + "is interrupted.");
            }
        }

        requestWorkData();
    }

    public void startBenchmark() {
        synchronized (BenchMonitor) {
            isReadyToBenchmark = true;
            BenchMonitor.notifyAll();
        }
    }

    public void startCalculate() {
        isReadyToCalculate = true;
        synchronized (CalculateMonitor) {
            CalculateMonitor.notifyAll();
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
                                   " returned not correct benchmark result. Request will be duplicated.");
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
