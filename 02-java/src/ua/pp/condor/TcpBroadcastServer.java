package ua.pp.condor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This server listens the specified port for new TCP connections
 * and will send incoming messages from any client to all other clients,
 * which are connected to this server at this moment.
 */
public final class TcpBroadcastServer {

    // We will use ConcurrentHashMap for thread-safe "put" and "remove" operations
    // while we iterate through whole collection
    private static final Map<PrintWriter, Object> connected = new ConcurrentHashMap<>();

    // Dummy value to associate with an Object in the backing Map
    private static final Object DUMMY = new Object();

    /**
     * A handler for each new connected client, which will receive data from socket
     * and send them to other connetced clients.
     */
    private static class Handler extends Thread {

        private final Socket socket;
        private final String hostAddress;
        private final BufferedReader reader;
        private final PrintWriter writer;

        Handler(Socket socket) throws IOException {
            if (socket == null) {
                throw new NullPointerException("socket can not be null");
            }
            this.socket = socket;

            hostAddress = socket.getInetAddress().getHostAddress();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            connected.put(writer, DUMMY);
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    String msg = reader.readLine();
                    if (msg == null) {
                        return;
                    }
                    final int connectionNumber = connected.size() - 1;
                    if (connectionNumber > 0) {
                        int sentTo = 0;
                        for (PrintWriter connectedWriter : connected.keySet()) {
                            if (connectedWriter != writer) {
                                connectedWriter.println(msg);
                                sentTo++;
                            }
                        }
                        System.out.println(String.format("%s sent message to %d user(s): %s",
                                hostAddress, sentTo, msg));
                    } else {
                        System.out.println(String.format(
                                "%s has tried to send message (%s), but here is no one connected", hostAddress, msg));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connected.remove(writer);
                try {
                    writer.close();
                    reader.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Disconnected: " + hostAddress + ", current connection number: " + connected.size());
            }
        }
    }

    /**
     * Send a message to a new connected client and close this connection
     * if the server is overloaded.
     */
    private static void closeConnection(Socket socket) throws IOException {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println("Server is overloaded. Please, try again later...");
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Incorrect argument number.\n" +
                    "Use: java TcpBroadcastServer portNumber maxConnectionNumber");
        }

        final int port = Integer.parseInt(args[0]);
        final int maxConnectionNumber = Integer.parseInt(args[1]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TcpBroadcastServer started!");

            for (;;) {
                Socket socket = serverSocket.accept();
                final int connectionNumber = connected.size() + 1;
                if (connectionNumber > maxConnectionNumber) {
                    closeConnection(socket);
                } else {
                    new Handler(socket).start();
                    System.out.println(String.format("Connected: %s, current connection number: %d",
                            socket.getInetAddress().getHostAddress(), connectionNumber));
                }
            }
        }
    }
}
