package serverreverseproxy;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
//import com.sun.management.*; daria alternativas para métodos para encontrar o cpu e ram
import java.lang.management.*;
import java.time.LocalTime;
import java.util.Random;

import static serverreverseproxy.HMAC.calculateRFC2104HMAC;

public class AgenteUDP {

    public static byte[] serialize(PDU_AM packet) {
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
        //Porta usada
        int port = 8888;

        //Endereço multicast
        InetAddress group = InetAddress.getByName("239.8.8.8");

        //Socket para o multicast
        MulticastSocket s = new MulticastSocket(port);

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

            //Descodifica os bytes para PDU_MA
            PDU_MA request = (PDU_MA) objectFromBytes(recv.getData());

            System.out.println("Received data from: " + recv.getAddress().toString() +
                    ":" + recv.getPort() + " with length: " +
                    recv.getLength());
            String receiveMsg = new String(recv.getData(), 0, 1024);
            //System.out.println("Data: " + receiveMsg);


            if(request != null){
                LocalTime reqLT = request.getTimestamp();
                String HMACResult = request. getHMAC_RESULT();
                String HMACCalc = calculateRFC2104HMAC(reqLT.toString(), key);
                System.out.println("Timestamp: " + reqLT + "\n"
                                    + "HMAC result received: " + HMACResult + "\n"
                                    + "HMAC calculated: " + HMACCalc + "\n"
                                    + "HMAC's match? " + HMACResult.equals(HMACCalc) + "\n");
            }


            //espera entre 0 e 10 ms para responder
            Thread.sleep(rand.nextInt(11));
        /*
            Envia resposta em unicast.

            Since one can send unicast packets using the same MulticastSocket
        instance as for ones multicasts, it makes sense to mention how unicasts
        are handled when there is more than one listener, which can only be when
        they are all on the same machine.
            Unicast traffic sent to the port will be received by only one of the
        listeners with a socket bound to the port. With my test setup, the last
        socket to bind to the port receives the unicast traffic.
        */

            //RAM usage
            long maxRam = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();
            long usedRam = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
            float ram = usedRam / maxRam;

            //CPU usage
            java.lang.management.OperatingSystemMXBean o = ManagementFactory.getOperatingSystemMXBean();
            float cpu = (float) o.getSystemLoadAverage();

            //timestamp
            LocalTime timestamp = LocalTime.now();

            //String address = new String(group.getAddress());


            PDU_AM resp = new PDU_AM(group.getAddress(), ram, cpu, timestamp, key);
            byte[] b = serialize(resp);

            //String sendData = "Resposta em unicast";
            //DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), recv.getAddress(), 8888);
            //s.send(sendPacket);
            DatagramPacket sendPacket = new DatagramPacket(b, b.length, recv.getAddress(), 8888);
            s.send(sendPacket);
            //byte[] receiveData = new byte[1024];
            //DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            //s.receive(receivePacket);
            
        }
        //Sai do grupo
        //s.leaveGroup(group);

        //Fecha o socket multicast
        //  s.close();
    }
}
