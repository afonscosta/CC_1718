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

    public void setPort(int port) {
        this.port = port;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getRtt() {
        return rtt;
    }

    public void setRtt(double rtt) {
        this.rtt = rtt;
    }

    public double getBw() {
        return bw;
    }

    public void setBw(double bw) {
        this.bw = bw;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void calcQuality(double pesoRAM, double pesoCPU, double pesoRTT, double pesoBW) {
        this.quality = ram * pesoRAM + cpu * pesoCPU + rtt * pesoRTT + bw * pesoBW;
    }

}
