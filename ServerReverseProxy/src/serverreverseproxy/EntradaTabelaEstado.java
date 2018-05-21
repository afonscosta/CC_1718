package serverreverseproxy;

import java.net.InetAddress;
import java.time.LocalTime;

public class EntradaTabelaEstado {

    private int port;
    private float ram;
    private float cpu;
    private long rtt;
    private float bw;

    public EntradaTabelaEstado(int port, float ram, float cpu, long rtt, float bw) {
        this.port = port;
        this.ram = ram;
        this.cpu = cpu;
        this.rtt = rtt;
        this.bw = bw;
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

    public long getRtt() {
        return rtt;
    }

    public void setRtt(long rtt) {
        this.rtt = rtt;
    }

    public float getBw() {
        return bw;
    }

    public void setBw(float bw) {
        this.bw = bw;
    }

}
