package serverreverseproxy;

import java.net.*;

import static java.lang.Thread.sleep;

public class MonitorUDP {
    public static void main(String args[]) throws Exception
    {
        //Pacote usado para receber as respostas em unicast ao pedido multicast
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        //Pedido em multicast
        String msg = "Request em multicast";

        //Porta usada
        int portMulticast = 8888;

        //Endereço multicast
        InetAddress group = InetAddress.getByName("239.8.8.8");

        //Socket para o multicast
        MulticastSocket s = new MulticastSocket(portMulticast);

        //Pacote com o pedido para ser enviado em multicast
        DatagramPacket sendDataMulticast = new DatagramPacket(msg.getBytes(), msg.length(), group, portMulticast);

        while(true)
        {
            //Manda o pedido em multicast
            s.send(sendDataMulticast);
            sleep(3000);

            /*
            Escuta possíveis respostas em unicast.
            MulticastSocket é uma subclasse de DatagramSocket e como tal tem a capacidade de
            receber pacotes unicast também.
            */
            s.receive(receivePacket);

            System.out.println("Vou imprimir!!!");
            //Recebeu uma resposta em unicast.
            System.out.println("Received data from: " + receivePacket.getAddress().toString() +
                    ":" + receivePacket.getPort() + " with length: " +
                    receivePacket.getLength());
            String receiveMsg = new String(receivePacket.getData(),0,1024);
            System.out.println("Data: " + receiveMsg);

            // And when we have finished sending data close the socket
//            s.close();
        }



    }
}
