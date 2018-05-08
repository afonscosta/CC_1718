package serverreverseproxy;

import java.net.InetAddress;
import java.time.LocalTime;

public class EntradaTabelaEstado {

    private int port;
    private float ram;
    private float cpu;
    private LocalTime rtt;
    private float bw;

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

    public LocalTime getRtt() {
        return rtt;
    }

    public void setRtt(LocalTime rtt) {
        this.rtt = rtt;
    }

    public float getBw() {
        return bw;
    }

    public void setBw(float bw) {
        this.bw = bw;
    }

}
