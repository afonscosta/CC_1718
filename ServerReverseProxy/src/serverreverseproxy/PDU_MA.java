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


public class PDU_MA implements Serializable {
    
    private LocalTime timestamp;
    /* chave para autenticação e controlo de erros de mensagens */
    private String HMAC_RESULT;

    /* CONSTRUCTOR */
    public PDU_MA(String key) {
        this.timestamp = LocalTime.now();
        try {
            this.HMAC_RESULT = calculateRFC2104HMAC(timestamp.toString(), key);
        } catch ( NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(PDU_MA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* GETTERS */
    public LocalTime getTimestamp() {
        return timestamp;
    }

    public String getHMAC_RESULT() {
        return HMAC_RESULT;
    }
    
}
