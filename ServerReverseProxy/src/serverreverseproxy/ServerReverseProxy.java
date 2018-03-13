/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class ServerReverseProxy {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		
		
        try {
            ServerSocket sSocket = new ServerSocket(12345);
            while (true) {
                Socket clSocket = sSocket.accept();
                Thread t = new Thread(new ServerWorker(clSocket));
                t.start();
            }

        } catch (IOException e) {
        }
    }

}
