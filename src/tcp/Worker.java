package tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worker extends Thread {
    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            WebRequest request = WebRequest.of(reader);
            System.out.println(request.command + " " + request.url);

            writer.write("HTTP/1.1 200 OK\n");
            writer.write("Content-Type: text/html\n\n");

            writer.write("Hello " + request.headers.get("User-Agent") + "! <br/>");
            writer.write("You requested: "+request.command + " " + request.url + " by using HTTP version "+request.version+"\n");
            writer.write("\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.flush();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class WebRequest {
        public String command;
        public String url;
        public String version;
        private Map<String, String> headers;

        public WebRequest(String command, String url, String version, Map<String, String> headers) {
            this.command = command;
            this.url = url;
            this.version = version;
            this.headers = headers;
        }

        public static WebRequest of(BufferedReader reader) throws IOException {
            List<String> input = new ArrayList<>();
            String line;
            while (!(line = reader.readLine()).equals("")) {
                input.add(line);
            }
            String[] args = input.get(0).split("\\s++");
            HashMap<String, String> headers = new HashMap<>();
            for (int i = 1; i < input.size(); i++) {
                String[] pair = input.get(i).split(":");
                headers.put(pair[0], pair[1]);
            }

            return new WebRequest(args[0], args[1], args[2], headers);
        }
    }
}
