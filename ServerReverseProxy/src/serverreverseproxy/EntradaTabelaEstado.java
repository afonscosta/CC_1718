package serverreverseproxy;

import java.net.InetAddress;
import java.time.LocalTime;

public class EntradaTabelaEstado {

    private int port;
    private double ram;
    private double cpu;
    private double rtt;
    private double bw;
    private double quality;


    public EntradaTabelaEstado(int port, double ram, double cpu, double rtt, double bw) {
        this.port = port;
        this.ram = ram;
        this.cpu = cpu;
        this.rtt = rtt;
        this.bw = bw;
        this.quality = Double.MAX_VALUE;
    }

    public int getPort() {
        return port;
    }

    public double getRam() {
        return ram;
    }

    public double getCpu() {
        return cpu;
    }

    public double getRtt() {
        return rtt;
    }

    public double getBw() {
        return bw;
    }

    public double getQuality() {
        return quality;
    }

    public void calcQuality(double pesoRAM, double pesoCPU, double pesoRTT, double pesoBW) {
        this.quality = ram * pesoRAM + cpu * pesoCPU + rtt * pesoRTT + bw * pesoBW;
    }

}
