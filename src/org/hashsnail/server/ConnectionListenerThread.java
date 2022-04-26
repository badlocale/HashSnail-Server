package org.hashsnail.server;

import java.net.*;
import java.io.*;
import java.util.*;

public final class ConnectionListenerThread implements Runnable {
    private List<Socket> clientSockets;
    private int port;

    public ConnectionListenerThread(int port, List<Socket> clientSockets) {
        if (1 <= port && port <= 65535) {
            this.port = port;
        } else {
            this.port = 8000;
        }
        this.clientSockets = clientSockets;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket clientSocket;
        boolean isQniqueAddress = true;

        try {
            serverSocket = new ServerSocket(8000);
            System.out.println(serverSocket); //
        } catch (IOException e) {
            System.out.println("Error when opening the socket...");
            return;
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();

                isQniqueAddress = true;
                for (Socket s: clientSockets) {
                    String storedAddress = s.getInetAddress().getHostName();
                    String newAddress = clientSocket.getInetAddress().getHostName();
                    if (storedAddress.equals(newAddress))
                        isQniqueAddress = false;
                }

                if (isQniqueAddress == true) {
                    clientSockets.add(clientSocket);
                    System.out.println(clientSockets.get(clientSockets.size() - 1) + " " + clientSockets.size()); //
                }

            } catch (IOException e) {
                System.out.println("Bind operation failed, or the socket is already bound...");
            }
        }
    }
}
