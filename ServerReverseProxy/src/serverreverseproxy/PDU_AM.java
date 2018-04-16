/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import static serverreverseproxy.HMAC.calculateRFC2104HMAC;
import java.lang.Float;


public class PDU_AM implements Serializable {
    
    private byte[] IP_origem;
    private float ram_usage;
    private float cpu_usage;
    private LocalTime timestamp;
    private String HMAC_RESULT;

    /* CONSTRUCTOR */
    public PDU_AM(byte[] IP_origem, float ram_usage, float cpu_usage, LocalTime timestamp, String key) {
        this.IP_origem = IP_origem;
        this.ram_usage = ram_usage;
        this.cpu_usage = cpu_usage;
        this.timestamp = timestamp;
        try {
            String ip = new String (this.IP_origem, StandardCharsets.UTF_8);
            String ram = Float.toString(this.ram_usage);
            String cpu = Float.toString(this.cpu_usage);
            String data = ip + this.timestamp.toString() + ram + cpu;
            this.HMAC_RESULT = calculateRFC2104HMAC(data , key);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(PDU_MA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* GETTERS */
    public byte[] getIP_origem() {
        return IP_origem;
    }

    public float getRam_usage() {
        return ram_usage;
    }

    public float getCpu_usage() {
        return cpu_usage;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getHMAC_RESULT() {
        return HMAC_RESULT;
    }
    
    /* SETTERS */
    public void setIP_origem(byte[] IP_origem) {
        this.IP_origem = IP_origem;
    }

    public void setRam_usage(float ram_usage) {
        this.ram_usage = ram_usage;
    }

    public void setCpu_usage(float cpu_usage) {
        this.cpu_usage = cpu_usage;
    }
    
    

}
