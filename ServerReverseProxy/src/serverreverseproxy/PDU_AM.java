/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverreverseproxy;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static serverreverseproxy.HMAC.calculateRFC2104HMAC;


public class PDU_AM implements Serializable {
    
    private float ram_usage;
    private float cpu_usage;
    private LocalTime timestamp;
    private String HMAC_RESULT;

    /* CONSTRUCTOR */
    public PDU_AM(float ram_usage, float cpu_usage, LocalTime timestamp, String key) {
        this.ram_usage = ram_usage;
        this.cpu_usage = cpu_usage;
        this.timestamp = timestamp;
        try {
            String ram = Float.toString(this.ram_usage);
            String cpu = Float.toString(this.cpu_usage);
            String data = this.timestamp.toString() + ram + cpu;
            this.HMAC_RESULT = calculateRFC2104HMAC(data , key);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(PDU_MA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* GETTERS */
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
    public void setRam_usage(float ram_usage) {
        this.ram_usage = ram_usage;
    }

    public void setCpu_usage(float cpu_usage) {
        this.cpu_usage = cpu_usage;
    }
    
    

}
