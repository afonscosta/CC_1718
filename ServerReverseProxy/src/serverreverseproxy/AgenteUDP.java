package serverreverseproxy;

import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalTime;
import java.util.Random;

import static serverreverseproxy.Converter.objectFromBytes;
import static serverreverseproxy.Converter.serialize;
import static serverreverseproxy.HMAC.calculateRFC2104HMAC;

public class AgenteUDP {

    public static void main(String args[]) throws Exception
    {
        //Porta do Servidor HTTP associado
        int portaHTTP;
        if (args.length == 1)
            portaHTTP = Integer.parseInt(args[0]);
        else
            portaHTTP = 80;


        //Porta usada
        int port = 8888;

        //Endereço multicast
        InetAddress group = InetAddress.getByName("239.8.8.8");

        //Socket para o multicast
        MulticastSocket s = new MulticastSocket(port);
        s.setTimeToLive(7);

        //Junta-se ao grupo multicast para receber pacotes
        s.joinGroup(group);

        //Pacote que vai receber o pedido multicast
        byte[] buf = new byte[1024];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        //gera valores aleatórios para atrasar a resposta
        Random rand = new Random();

        //key
        String key = "key";

        while(true) {

            //Recebe o pedido multicast
            s.receive(recv);

            if (objectFromBytes(recv.getData()).getClass().getSimpleName().equals("PDU_MA")){
                //Descodifica os bytes para PDU_MA
                PDU_MA request = (PDU_MA) objectFromBytes(recv.getData());

                System.out.println("Received data from: " + recv.getAddress().toString() +
                        ":"                    + recv.getPort() +
                        " with length: "       + recv.getLength());

                if (request != null) {
                    LocalTime reqLT = request.getTimestamp();
                    String HMACResult = request.getHMAC_RESULT();
                    String HMACCalc = calculateRFC2104HMAC(reqLT.toString(), key);
                    System.out.println("Timestamp: " + reqLT + "\n"
                            + "HMAC result received: " + HMACResult + "\n"
                            + "HMAC calculated: " + HMACCalc + "\n"
                            + "HMAC's match? " + HMACResult.equals(HMACCalc) + "\n");
                }


                //espera entre 0 e 10 ms para responder
                Thread.sleep(rand.nextInt(11));

                //Envia resposta em unicast.

                //RAM usage
                Runtime obj = Runtime.getRuntime();
                double freeRAM = obj.freeMemory();
                double maxRAM = obj.maxMemory();
                double ram = freeRAM/maxRAM;

                //CPU usage
                com.sun.management.OperatingSystemMXBean o = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
                double cpu = o.getProcessCpuLoad();

                //timestamp
                LocalTime timestamp = request.getTimestamp();

                PDU_AM resp = new PDU_AM(portaHTTP, ram, cpu, timestamp, key);
                byte[] b = serialize(resp);

                //String sendData = "Resposta em unicast";
                DatagramPacket sendPacket = new DatagramPacket(b, b.length, recv.getAddress(), 8888);
                s.send(sendPacket);
            }
        }
    }
}
