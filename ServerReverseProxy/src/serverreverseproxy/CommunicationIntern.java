package serverreverseproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationIntern implements Runnable {

    private BufferedReader inFromServer;
    private PrintWriter outToClient;

    public CommunicationIntern(Socket socketInterno, BufferedReader inFromServer, PrintWriter outToClient) {

        this.inFromServer = inFromServer;
        this.outToClient = outToClient;

    }

    @Override
    public void run() {
        String clientSentence;
        String serverSentence;
        try{
            //Ler a resposta do HTTP SERVER
            while((serverSentence = inFromServer.readLine()) != null) {
                outToClient.println(serverSentence);

            }
        } catch(IOException e) {

        }
    }
}
