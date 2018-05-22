/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ServerWorker implements Runnable {

    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado;

    public ServerWorker(Socket socketExterno, HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado) {

        this.TabelaEstado = TabelaEstado;
        System.out.println("IP do servidor escolhido: ");

        try {

            this.inFromClient = new BufferedReader(new InputStreamReader(socketExterno.getInputStream()));
            this.outToClient = new DataOutputStream(socketExterno.getOutputStream());

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

    private static String sendGET(String clientSentence) throws IOException {
        URL obj = new URL(clientSentence);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
//        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            return response.toString();
        } else {
            System.out.println("GET request not worked");
        }
        return "";

    }

    public void run() {

        String clientSentence;
        String serverSentence;


//        while (true){

            try{

                clientSentence = inFromClient.readLine();
//                System.out.println("Received: " + clientSentence);

                //Consultar a tabela de estado
                InetAddress ip = calcMelhorServidor();

                System.out.println("IP do servidor escolhido: " + ip);

                //Estabelecer a conexão TCP com o HTTP SERVER
//                Socket socketInterno = new Socket(ip, TabelaEstado.get(ip).getPort());


                serverSentence = sendGET(clientSentence);

                //Inicializa os meios de comunicação com o HTTP SERVER
//                this.inFromServer = new BufferedReader(new InputStreamReader(socketInterno.getInputStream()));
//                this.outToServer = new DataOutputStream(socketInterno.getOutputStream());

                //Realizar pedido ao HTTP SERVER
//                outToServer.writeBytes(clientSentence);

                //Ler a resposta do HTTP SERVER
//                serverSentence = inFromServer.readLine();

                //Devolver a resposta ao Cliente
                outToClient.writeBytes(serverSentence);

            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }
}
