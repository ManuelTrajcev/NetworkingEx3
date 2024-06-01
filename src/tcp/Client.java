package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {
    private int serverPort;
    private String serverName;

    public Client(int serverPort, String serverName) {
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            socket = new Socket(InetAddress.getByName(serverName), this.serverPort);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("GET / HTTP/1.1\n");
            writer.write("Host: developer.mozilla.org\n");
            writer.write("User-Agent: OSClient\n");
            writer.write("\n");
            writer.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Client received: " + line);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String serverName = System.getenv("SERVER_NAME");
        String serverPort = System.getenv("SERVER_PORT");
        if (serverPort == null) {
            throw new RuntimeException("Server port should be defined as ENV {SERVER_PORT}.");
        }
        Client client = new Client(Integer.parseInt(serverPort), serverName);
        client.start();
    }
}
