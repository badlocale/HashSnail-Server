package org.hashsnail.server.net;

import org.hashsnail.server.model.Algorithm;
import org.hashsnail.server.model.mods.AttackMode;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerSession implements Runnable {
    private Socket socket = null;
    private AttackMode attackMode = null;
    private Algorithm algorithm = null;

    private double benchmarkResult = -1;

    private final Object CalculateMonitor = new Object();
    private Boolean isReadyToCalculate = false;

    public ServerSession(Socket socket, AttackMode attackMode, Algorithm algorithm) {
        this.socket = socket;
        this.attackMode = attackMode;
        this.algorithm = algorithm;
    }

    @Override
    public void run() {
        try {
            benchmarkResult = requestBenchmark(10);
        } catch (IOException e) {
            System.err.println("Cant send benchmark request for client by address " + socket.getInetAddress() + ".");
        }

        if (benchmarkResult > 0) {
            isReadyToCalculate = true;
        }

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

        try {
            requestWorkData();
        } catch (IOException e) {
            e.printStackTrace();//todo
        }
    }

    public void startCalculate() {
        synchronized (CalculateMonitor) {
            CalculateMonitor.notifyAll();
        }
    }

    private double requestBenchmark(int sec) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String[] clientsResponse = null;
        double benchResult = 0;


        while (benchResult <= 0) {
            byte[] request = (new String((byte)PocketType.BENCHMARK_REQUEST.ordinal() + " " +
                                         sec).getBytes(StandardCharsets.UTF_8));
            outputStream.write(request);
            outputStream.flush();

            try {
                clientsResponse = reader.readLine().split(" ");

                if (Integer.parseInt(clientsResponse[0]) == PocketType.BENCHMARK_RESULT.ordinal()
                        && clientsResponse.length > 1) {
                    benchResult = Double.parseDouble(clientsResponse[1]);
                }
            } catch (Exception e) {
                System.err.println("Client by address " + socket.getInetAddress() +
                                   " returned not correct benchmark result. Request will be duplicated.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return benchResult;
    }

    private void requestWorkData() throws IOException {

    }
}

enum PocketType {
    BENCHMARK_REQUEST,
    BENCHMARK_RESULT,
    INITIAL_DATA,
    RESULTS
}
