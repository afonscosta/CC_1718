/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ServerWorker implements Runnable {

    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private BufferedReader inFromServer;
    private PrintWriter outToServer;
    private HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado;
    private Socket socketExterno;

    public ServerWorker(Socket socketExterno, HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado) {

        this.TabelaEstado = TabelaEstado;
	this.socketExterno = socketExterno;

        try {

            this.inFromClient = new BufferedReader(new InputStreamReader(socketExterno.getInputStream()));
            this.outToClient = new PrintWriter(socketExterno.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Erro no establecimento da ligação.");
        }
    }

    private InetAddress calcMelhorServidor() {
        Iterator<Map.Entry<InetAddress, EntradaTabelaEstado>> it = TabelaEstado.entrySet().iterator();
        Map.Entry<InetAddress, EntradaTabelaEstado> entry;
        InetAddress ipRes = null;
        double minQuality = Double.MAX_VALUE;
        double tempQuality;

        while(it.hasNext()) {
            entry = it.next();
            tempQuality = entry.getValue().getQuality();
            if (tempQuality < minQuality) {
                minQuality = tempQuality;
                ipRes = entry.getKey();
            }
        }
        return ipRes;
    }

    public void run() {

        String clientSentence;
        String serverSentence;

        try{

            //Consultar a tabela de estado
            InetAddress ip = calcMelhorServidor();

            System.out.println("IP do servidor escolhido: " + ip);
		
            if (TabelaEstado.get(ip).getStatus().equals("ativo")) {

                //Estabelecer a conexão TCP com o HTTP SERVER
                Socket socketInterno = new Socket(ip, TabelaEstado.get(ip).getPort());

                //Inicializa os meios de comunicação com o HTTP SERVER
                this.inFromServer = new BufferedReader(new InputStreamReader(socketInterno.getInputStream()));
                this.outToServer = new PrintWriter(socketInterno.getOutputStream(), true);

                Thread t1 = new Thread(new RequestIntern(socketInterno, inFromClient, outToServer));
                t1.start();

                Thread t = new Thread(new CommunicationIntern(socketInterno, inFromServer, outToClient));
                t.start();

                //Realizar pedido ao HTTP SERVER
                t1.join();
                t.join();

                System.out.println();

                socketInterno.close();

                //Devolver a resposta ao Cliente
                socketExterno.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
