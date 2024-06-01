package shared;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SharedResourceServer extends Thread {
    private String csvFile;
    private String counterFile;
    private int port;

    public SharedResourceServer(String csvFile, String counterFile, int port) {
        this.csvFile = csvFile;
        this.counterFile = counterFile;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            execute();
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute() throws IOException {
        System.out.println("Shared resource started...");
        ServerSocket serverSocket = null;


        serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client arrived");
            new Worker(socket, new File(csvFile), new File(counterFile)).start();
        }
    }

    public static void main(String[] args) {
        String  serverPort = System.getenv("SERVER_PORT");
        if (serverPort == null || serverPort.isEmpty()) {
            throw new RuntimeException("Please add env variable with port number.");
        }
        SharedResourceServer server = new SharedResourceServer(
                System.getenv("logFile"),
                System.getenv("counterFile"),
                Integer.parseInt(serverPort));
        server.start();
    }
}
