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

    private int portaHTTP;
    private double ram_usage;
    private double cpu_usage;
    private LocalTime timestamp;
    private String HMAC_RESULT;

    /* CONSTRUCTOR */
    public PDU_AM(int portaHTTP, double ram_usage, double cpu_usage, LocalTime timestamp, String key) {
        this.portaHTTP = portaHTTP;
        this.ram_usage = ram_usage;
        this.cpu_usage = cpu_usage;
        this.timestamp = timestamp;
        try {
            String porta = Integer.toString(this.portaHTTP);
            String ram = Double.toString(this.ram_usage);
            String cpu = Double.toString(this.cpu_usage);
            String data = this.timestamp.toString() + ram + cpu + porta;
            this.HMAC_RESULT = calculateRFC2104HMAC(data , key);
        } catch ( NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(PDU_MA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* GETTERS */
    public int getPortaHTTP() { return portaHTTP; }

    public double getRam_usage() {
        return ram_usage;
    }

    public double getCpu_usage() {
        return cpu_usage;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getHMAC_RESULT() {
        return HMAC_RESULT;
    }

}
