/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class ServerWorker implements Runnable {

    private BufferedReader inFromClient;
    private PrintWriter out;
    private Socket socket;
    private String clientSentence;

    public ServerWorker(Socket socket) {

        this.socket = socket;

        try {

            this.inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Erro no establecimento da ligação.");
        }
    }

    public void run() {
      while (true){
        System.out.println("Estou em espera!!!");
        clientSentence = inFromClient.readLine();
        System.out.println("Received: " + clientSentence);

        //Consultar a tabela de estado

        //Estabelecer a conexão TCP com o HTTP SERVER
        try{
          Socket clientSocket = new Socket("localhost", 80);
        }
      } catch (IOException e) {
          ;
      }
        //Ler a resposta do HTTP SERVER

        //Devolver a resposta ao Cliente
        }
      }
}
