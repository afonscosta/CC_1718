/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class ServerReverseProxy {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado = new HashMap<>();

            ServerSocket ssExterno = new ServerSocket(12345);
            //incialização da thread MonitorUDP sempre que o ServerReverseProxy é iniciado
            Thread m = new Thread(new MonitorUDP(TabelaEstado));
            m.start();

            while (true) {
                Socket clientSocket = ssExterno.accept();
                Thread t = new Thread(new ServerWorker(clientSocket, TabelaEstado));
                t.start();
            }

        } catch (IOException e) {
        }
    }

}
