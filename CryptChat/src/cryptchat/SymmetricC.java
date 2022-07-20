/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author Vuk
 */
public class SymmetricC {

    public static final String AES = "AES";
    private SecretKey key;
    private boolean hasKey = false;

    public SymmetricC(SecretKey key) {
        this.key = key;
        this.hasKey = true;
    }
    
    public SymmetricC(String keyHex){
        byte[] bytes = Hex.decode(keyHex);
        SecretKeySpec skc = new SecretKeySpec(bytes, "AES");
        this.key = skc;
        this.hasKey = true;
    }
    
    public SymmetricC(byte[] keyBytes){
        SecretKeySpec skc = new SecretKeySpec(keyBytes, "AES");
        this.key = skc;
        this.hasKey = true;
    }

    public SymmetricC() {
        this.createNewKey();
    }

    private SecretKey createAESKey() {
        try {
            SecureRandom sr = new SecureRandom();
            KeyGenerator kg = KeyGenerator.getInstance(AES);
            kg.init(256, sr);
            SecretKey key = kg.generateKey();

            return key;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void createNewKey() {
        this.key = this.createAESKey();
        this.hasKey = true;
    }

    public byte[] getKeyBytes(){
        return this.key.getEncoded();
    }
    
    public SecretKey getKey() {
        return this.key;
    }
    
    public String getKeyHex(){
        return Hex.toHexString(this.key.getEncoded());
    }

    public String encryptMessage(String plain) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/Pkcs5Padding");
            c.init(Cipher.ENCRYPT_MODE, this.key);
            return Hex.toHexString(c.doFinal(plain.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String decryptMessage(String cipher) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/Pkcs5Padding");
            c.init(Cipher.DECRYPT_MODE, this.key);
            return new String(c.doFinal(Hex.decode(cipher)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
/////////      ISTO KAO GORE ALI SA BYTE-OVIMA         ////////////////////
    
    public byte[] encryptMessage2byte(String plain) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/Pkcs5Padding");
            c.init(Cipher.ENCRYPT_MODE, this.key);
            return c.doFinal(plain.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String decryptMessage(byte[] cipher) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/Pkcs5Padding");
            c.init(Cipher.DECRYPT_MODE, this.key);
            return new String(c.doFinal(Hex.decode(cipher)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(SymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
