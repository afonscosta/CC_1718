package serverreverseproxy;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.util.HashMap;

import static java.lang.Thread.sleep;
import static serverreverseproxy.Converter.*;
import static serverreverseproxy.HMAC.calculateRFC2104HMAC;

import static java.lang.Thread.sleep;

public class MonitorUDP implements Runnable {

    private HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado;

    public MonitorUDP(HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado) {
        this.TabelaEstado = TabelaEstado;
    }

    public void run()
    {
        String portaHTTPIN;
        String ramIN;
        String cpuIN;
        String timeIN;
        String hmacIN;
        String hmac;

        try {
            //Pacote usado para receber as respostas em unicast ao pedido multicast
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            //Porta usada
            int portMulticast = 8888;

            //Endereço multicast
            InetAddress group = InetAddress.getByName("239.8.8.8");

            //Socket para o multicast
            MulticastSocket s = new MulticastSocket(portMulticast);
            s.setSoTimeout(5000);   // set the timeout in millisecounds.
            s.setTimeToLive(7);

            //Pedido em multicast

            while (true) {
                PDU_MA pduEnv = new PDU_MA("key");
                byte[] pduEnvSerialized = serialize(pduEnv);
                if (pduEnvSerialized != null) {

                    //Pacote com o pedido para ser enviado em multicast
                    DatagramPacket sendDataMulticast = new DatagramPacket(
                            pduEnvSerialized,
                            pduEnvSerialized.length,
                            group,
                            portMulticast
                    );

                    //Manda o pedido em multicast
                    s.send(sendDataMulticast);
                    /*
                    Escuta possíveis respostas em unicast.
                    MulticastSocket é uma subclasse de DatagramSocket e como tal tem a capacidade de
                    receber pacotes unicast também.
                    */

                    try {
                        while (true) {
                            s.receive(receivePacket);
                            PDU_AM pduReceived = (PDU_AM) objectFromBytes(receivePacket.getData());

                            //Recebeu uma resposta em unicast.
                            System.out.println(
                                    "Received data from: " + receivePacket.getAddress().toString() +
                                            ":" + receivePacket.getPort() + " with length: " +
                                            receivePacket.getLength()
                            );

                            if (pduReceived != null) {
                                portaHTTPIN = String.valueOf(pduReceived.getPortaHTTP());
                                ramIN = String.valueOf(pduReceived.getRam_usage());
                                cpuIN = String.valueOf(pduReceived.getCpu_usage());
                                timeIN = pduReceived.getTimestamp().toString();
                                hmacIN = String.valueOf(pduReceived.getHMAC_RESULT());
                                hmac = calculateRFC2104HMAC(timeIN + ramIN + cpuIN + portaHTTPIN, "key");
                                System.out.println(

                                        "HTTP Server Port: " + portaHTTPIN + "\n" +
                                                "RAM: " + ramIN + "\n" +
                                                "CPU: " + cpuIN + "\n" +
                                                "Timestamp: " + timeIN + "\n" +
                                                "HMAC result received: " + hmacIN + "\n" +
                                                "HMAC result calculated: " + hmac + "\n" +
                                                "HMAC iguais? " + hmacIN.equals(hmac) + "\n"
                                );

                                if (hmacIN.equals(hmac)) {
                                    long rtt = (LocalTime.now().toNanoOfDay() - pduReceived.getTimestamp().toNanoOfDay()) / 1000000;
                                    System.out.println("Round-Trip Time = " + rtt + " milliseconds.\n");
                                }
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Timeout reached!!! " + e);
                    }
                }

                sleep(5000);

                // And when we have finished sending data close the socket
                //                s.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
