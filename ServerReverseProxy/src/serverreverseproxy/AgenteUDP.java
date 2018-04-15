package serverreverseproxy;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
//import com.sun.management.*; daria alternativas para métodos para encontrar o cpu e ram
import java.lang.management.*;
import java.time.LocalTime;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

public class AgenteUDP {
    // toByteArray and toObject are taken from: http://www.java2s.com/Code/Java/File-Input-Output/Convertobjecttobytearrayandconvertbytearraytoobject.htm
    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
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

        Random rand = new Random();

        //while(true) {
            //Recebe o pedido multicast
            s.receive(recv);
            System.out.println("Received data from: " + recv.getAddress().toString() +
                    ":" + recv.getPort() + " with length: " +
                    recv.getLength());
            String receiveMsg = new String(recv.getData(), 0, 1024);
            System.out.println("Data: " + receiveMsg);

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

            byte[] address = group.getAddress();

            //key
            String key = address.toString() + Float.toString(ram) + Float.toString(cpu) + timestamp.toString();

            PDU_AM resp = new PDU_AM(address, ram, cpu, timestamp, key);
            byte[] b = toByteArray(resp);

            //String sendData = "Resposta em unicast";
            //DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), recv.getAddress(), 8888);
            //s.send(sendPacket);
            DatagramPacket sendPacket = new DatagramPacket(b, b.length, recv.getAddress(), 8888);
            s.send(sendPacket);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            s.receive(receivePacket);
        }
        //Sai do grupo
        //s.leaveGroup(group);

        //Fecha o socket multicast
        //  s.close();
    //}
}
