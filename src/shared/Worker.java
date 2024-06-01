package shared;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Worker extends Thread{
    private Socket socket;
    private File logFile;
    private File clientCountFile;
    private static Semaphore counterSemaphore = new Semaphore(1);

    public Worker(Socket socket, File logFile, File clientCountFile) {
        this.socket = socket;
        this.logFile = logFile;
        this.clientCountFile = clientCountFile;
    }

    private void execute() throws IOException, InterruptedException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFile)));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        RandomAccessFile clientCounterRaf = new RandomAccessFile(this.clientCountFile, "rw");
        Integer currClientCount = incrementCounter(clientCounterRaf);
        System.out.println("Total clients: " + currClientCount);
        String line;
        while ((line=reader.readLine()) != null) {
            writer.append(line + "\n");
        }
        writer.flush();
        if (writer!= null) {
            writer.flush();
            writer.close();
        }
        if (reader!= null) {
            reader.close();
        }
        if (socket!=null) {
            socket.close();
        }
    }

    private Integer incrementCounter(RandomAccessFile clientCounterRaf) throws InterruptedException, IOException {
        counterSemaphore.acquire();
        Integer currentClientCounter = clientCounterRaf.readInt();
        currentClientCounter++;
        clientCounterRaf.seek(0);
        clientCounterRaf.writeInt(currentClientCounter);
        counterSemaphore.release();
        return currentClientCounter;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
