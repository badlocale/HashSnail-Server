package org.hashsnail.server.net;

import java.net.*;
import java.io.*;
import java.util.*;

public final class ConnectionHandler implements Runnable {
    private List<Socket> clientSockets;
    private int port;

    public ConnectionHandler(int port, List<Socket> clientSockets) {
        if (1 <= port && port <= 65535) {
            this.port = port;
        } else {
            this.port = 8000;
        }
        this.clientSockets = clientSockets;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket clientSocket;
        boolean isUniqueAddress = true;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error when opening the socket...");
            System.exit(-1);
            return;
        }

        System.out.println("Port " + port + " is listening now by address: " + serverSocket.getInetAddress());

        while (true) {
            try {
                clientSocket = serverSocket.accept();

                isUniqueAddress = true;
                for (Socket s: clientSockets) {
                    String storedAddress = s.getInetAddress().getHostName();
                    String newAddress = clientSocket.getInetAddress().getHostName();
                    if (storedAddress.equals(newAddress))
                        isUniqueAddress = false;
                }

                if (isUniqueAddress == true) {
                    clientSockets.add(clientSocket);
                    System.out.println("IP: " + clientSockets.get(clientSockets.size() - 1).getInetAddress().toString() +
                                       " connected. Clients total: " + clientSockets.size()); //
                }

            } catch (IOException e) {
                System.err.println("Bind operation failed, or the socket is already bound...");
            }
        }
    }
}
