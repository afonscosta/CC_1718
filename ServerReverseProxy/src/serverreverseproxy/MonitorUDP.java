package serverreverseproxy;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static serverreverseproxy.HMAC.calculateRFC2104HMAC;

public class MonitorUDP {

    public static byte[] serialize(PDU_MA packet) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(packet);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Object objectFromBytes(byte[] packet_bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(packet_bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) throws Exception
    {
        String ipIN;
        String ramIN;
        String cpuIN;
        String timeIN;
        String hmacIN;
        String hmac;

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

        //Pedido em multicast
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

            while(true)
            {
                //Manda o pedido em multicast
                s.send(sendDataMulticast);

                /*
                Escuta possíveis respostas em unicast.
                MulticastSocket é uma subclasse de DatagramSocket e como tal tem a capacidade de
                receber pacotes unicast também.
                */
//                s.receive(receivePacket);

                try {
                    while(true) {
                        s.receive(receivePacket);
                        PDU_AM pduReceived = (PDU_AM) objectFromBytes(receivePacket.getData());

                        //Recebeu uma resposta em unicast.
                        System.out.println(
                                "Received data from: " + receivePacket.getAddress().toString() +
                                        ":" + receivePacket.getPort() + " with length: " +
                                        receivePacket.getLength()
                        );

                        if (pduReceived != null) {
                            ipIN = InetAddress.getByAddress(pduReceived.getIP_origem()).getHostAddress();
                            ramIN = String.valueOf(pduReceived.getRam_usage());
                            cpuIN = String.valueOf(pduReceived.getCpu_usage());
                            timeIN = pduReceived.getTimestamp().toString(); //Tentativa de melhorar
                            hmacIN = String.valueOf(pduReceived.getHMAC_RESULT());
                            hmac = calculateRFC2104HMAC(ipIN + timeIN + ramIN + cpuIN, "key");
                            System.out.println(
                                    "IP origem: " + ipIN + "\n" +
                                            "RAM: " + ramIN + "\n" +
                                            "CPU: " + cpuIN + "\n" +
                                            "Timestamp: " + timeIN + "\n" +
                                            "HMAC result received: " + hmacIN + "\n" +
                                            "HMAC result calculated: " + hmac + "\n" +
                                            "HMAC iguais? " + hmacIN.equals(hmac) + "\n"
                            );
                        }
                    }
                }
                catch (SocketTimeoutException e) {
                    // timeout exception.
                    System.out.println("Timeout reached!!! " + e);
//                    s.close();
                }

                sleep(5000);

                // And when we have finished sending data close the socket
//                s.close();
            }
        }
    }
}
