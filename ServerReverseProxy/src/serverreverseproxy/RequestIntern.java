package serverreverseproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestIntern implements Runnable {

    private BufferedReader inFromClient;
    private PrintWriter outToServer;
    private Socket socketInterno;

    public RequestIntern(Socket socketInterno, BufferedReader inFromClient, PrintWriter outToServer) {
        this.inFromClient = inFromClient;
        this.outToServer = outToServer;
        this.socketInterno = socketInterno;
    }
    public void run() {
        String clientSentence;
        try {
            while ((clientSentence = inFromClient.readLine()) != null) {
                outToServer.println(clientSentence);

            }

        } catch(IOException e) {

        }
    }
}
