package serverreverseproxy;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class AgenteUDP {
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

        //Recebe o pedido multicast
        s.receive(recv);
        System.out.println("Received data from: " + recv.getAddress().toString() +
                ":" + recv.getPort() + " with length: " +
                recv.getLength());
        String receiveMsg = new String(recv.getData(),0,1024);
        System.out.println("Data: " + receiveMsg);


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
        int i = 0;
        while (i < 1001000000) {
            String sendData = "Resposta em unicast";
            DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), recv.getAddress(), 8888);
            s.send(sendPacket);
            System.out.println("Enviei resposta de volta!!!");
            i++;
        }
        //Sai do grupo
        s.leaveGroup(group);

        //Fecha o socket multicast
        s.close();
    }
}
