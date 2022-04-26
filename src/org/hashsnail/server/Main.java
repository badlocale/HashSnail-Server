package org.hashsnail.server;

import org.hashsnail.server.model.range.PasswordRange;

import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class Main {
    private static List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<Socket>());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Future future = executor.submit(new ConnectionListenerThread(8000, clientSockets));

        PasswordRange range = new PasswordRange("%U%U%U%U".toCharArray());
        System.out.println(new String(range.subdivide(0f)) + " " +
                           new String(range.subdivide(0.000001f)) + " " +
                           new String(range.subdivide(0.14f)) + " " +
                           new String(range.subdivide(0.43f)) + " " +
                           new String(range.subdivide(1f)) + " " +
                           range.toString());

        PasswordRange range1 = new PasswordRange("%*#r%D#3".toCharArray());
        System.out.println(new String(range1.subdivide(0f)) + " " +
                           new String(range1.subdivide(0.000001f)) + " " +
                           new String(range1.subdivide(0.14f)) + " " +
                           new String(range1.subdivide(0.43f)) + " " +
                           new String(range1.subdivide(1f)) + " " +
                           range1.toString());
    }
}
