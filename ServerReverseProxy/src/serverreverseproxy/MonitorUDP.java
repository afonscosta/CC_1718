package serverreverseproxy;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;
import static serverreverseproxy.Converter.*;
import static serverreverseproxy.HMAC.calculateRFC2104HMAC;

import static java.lang.Thread.sleep;

public class MonitorUDP implements Runnable {

    private HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado;
    private HashMap<InetAddress, Integer> tabelaInatividade;

    public MonitorUDP(HashMap<InetAddress, EntradaTabelaEstado> TabelaEstado) {
        this.TabelaEstado = TabelaEstado;
        this.tabelaInatividade = new HashMap<>();

    }

    public void run()
    {
        String portaHTTPIN = "NA";
        String ramIN = "NA";
        String cpuIN = "NA";
        String timeIN = "NA";
        String hmacIN = "NA";
        String hmac = "NA";
        double rtt = Double.MAX_VALUE;

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

                    //incrementa os contadores a dizer que fica a espera da resposta dos agentes
                    for(InetAddress address : tabelaInatividade.keySet()) {
                        int count = tabelaInatividade.get(address);
                        tabelaInatividade.put(address, count + 1);
                    }


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
                                    rtt = (LocalTime.now().toNanoOfDay() - pduReceived.getTimestamp().toNanoOfDay()) / 1000000;
                                    System.out.println("Round-Trip Time = " + rtt + " milliseconds.\n");

                                    //atualização da tabela quando é recebida uma nova mensagem de estado do agente VERIFICAR O CALCULO DA BW
                                    EntradaTabelaEstado e = new EntradaTabelaEstado(Integer.parseInt(portaHTTPIN), Double.parseDouble(ramIN), Double.parseDouble(cpuIN), rtt, receivePacket.getLength()/(rtt/2));
                                    e.calcQuality(0.2, 0.3, 0.3,0.2);
                                    TabelaEstado.put(receivePacket.getAddress(), e);

                                    //a mensagem é recebida e é introduzido na tabelaInatividade a 0
                                    tabelaInatividade.put(receivePacket.getAddress(), 0);
                                }
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Timeout reached!!! " + e);
                    }
                }

                //quando um agente não responde a 3 pedidos de status é removido
                Iterator<InetAddress> it = tabelaInatividade.keySet().iterator();
                while(it.hasNext()) {
                    InetAddress key = it.next();
                    if (tabelaInatividade.get(key) >= 3){
                        TabelaEstado.remove(key);
                        it.remove();
                    }
                }

                System.out.println("===== TABELA DE INATIVIDADE =====");
                for(InetAddress address : tabelaInatividade.keySet()){
                    System.out.println("Address: " + address + " -> Count: " + tabelaInatividade.get(address));
                }
                System.out.println("=================================\n");


                System.out.println("======= TABELA DE ESTADO =======");
                for(InetAddress address : TabelaEstado.keySet()){
                    System.out.println("Address: " + address +
                                     " | Port: " + TabelaEstado.get(address).getPort() +
                                     " | RAM: "  + TabelaEstado.get(address).getRam() +
                                     " | CPU: "  + TabelaEstado.get(address).getCpu() +
                                     " | RTT: "  + TabelaEstado.get(address).getRtt() +
                                     " | BW: "   + TabelaEstado.get(address).getBw());
                }
                System.out.println("================================\n");


                sleep(2500);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
