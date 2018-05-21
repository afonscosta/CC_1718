package serverreverseproxy;

import java.net.InetAddress;
import java.time.LocalTime;

public class EntradaTabelaEstado {

    private int port;
    private float ram;
    private float cpu;
    private double rtt;
    private double bw;
    private float quality;


    public EntradaTabelaEstado(int port, float ram, float cpu, double rtt, double bw) {
        this.port = port;
        this.ram = ram;
        this.cpu = cpu;
        this.rtt = rtt;
        this.bw = bw;
        this.quality = Float.MAX_VALUE;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getRam() {
        return ram;
    }

    public void setRam(float ram) {
        this.ram = ram;
    }

    public float getCpu() {
        return cpu;
    }

    public void setCpu(float cpu) {
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

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public void calcQuality(float pesoRAM, float pesoCPU, float pesoRTT, float pesoBW) {
        this.quality = ram * pesoRAM + cpu * pesoCPU + (float)rtt * pesoRTT + (float)bw * pesoBW;
    }

}
