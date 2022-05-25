package org.hashsnail.server.net;

import java.net.*;
import java.io.*;
import java.util.*;

public final class ConnectionHandler implements Runnable {
    private final List<Socket> clientSockets;
    private final int port;

    public ConnectionHandler(int port, List<Socket> clientSockets) {
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
        boolean isUniqueAddress;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("[Connections] Error when opening the socket...");
            System.exit(-1);
            return;
        }

        try {
            System.out.println("[Connections] Port " + port + " is listening now by address: " +
                    InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("[Connections] Cant get host IP address.");
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();

                isUniqueAddress = true;
                for (Socket s: clientSockets) {
                    String storedAddress = s.getInetAddress().getHostAddress();
                    String newAddress = clientSocket.getInetAddress().getHostName();
                    if (storedAddress.equals(newAddress))
                        isUniqueAddress = false;
                }

                if (isUniqueAddress) {
                    clientSockets.add(clientSocket);
                    System.out.println("[Connections] New client with IP: " +
                            clientSockets.get(clientSockets.size() - 1).getInetAddress().getHostAddress() +
                            " connected. Clients total: " + clientSockets.size()); //
                }
            } catch (IOException e) {
                System.err.println("[Connections] Bind operation failed, or the socket is already bound...");
            }
        }
    }
}
