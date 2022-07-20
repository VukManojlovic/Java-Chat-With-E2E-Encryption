/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author Vuk
 */
public class AsymmetricC {

    private static final String RSA = "RSA";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public AsymmetricC() {
        
    }
    
    public void createNewKeys(){
        KeyPair kp = this.generateNewKeyPair();
        this.privateKey = kp.getPrivate();
        this.publicKey = kp.getPublic();
    }

    private KeyPair generateNewKeyPair() {
        try {
            SecureRandom sr = new SecureRandom();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(2048, sr);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String encryptMessage(String plain, String publicKeyHex) {
        try {
            PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(this.hex2bytes(publicKeyHex)));
            Cipher c = Cipher.getInstance(RSA);
            c.init(Cipher.ENCRYPT_MODE, pk);
            byte[] bytes = c.doFinal(plain.getBytes());
            return Hex.toHexString(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String decryptMessage(String cipher) {
        try {
            Cipher c = Cipher.getInstance(RSA);
            c.init(Cipher.DECRYPT_MODE, this.privateKey);
            byte[] decoded = c.doFinal(this.hex2bytes(cipher));
            return new String(decoded);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException  ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    ///////  ABOVE IS WITH STRING , BLOW IS WITH BYTES /////////////////////
    public byte[] encryptMessage(byte[] plain, byte[] publicKeyBytes) {
        try {
            PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Cipher c = Cipher.getInstance(RSA);
            c.init(Cipher.ENCRYPT_MODE, pk);
            byte[] bytes = c.doFinal(plain);
            return bytes;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public byte[] decryptMessage2byte(byte[] cipher) {
        try {
            Cipher c = Cipher.getInstance(RSA);
            c.init(Cipher.DECRYPT_MODE, this.privateKey);
            byte[] decoded = c.doFinal(cipher);
            return decoded;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException  ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AsymmetricC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getPublicKeyHex() {
        String keyHex = Hex.toHexString(this.publicKey.getEncoded());
        return keyHex;
    }

    public byte[] getPublicBytes() {
        return this.publicKey.getEncoded();
    }
    
    public String bytes2hex(byte[] bytes){
        return Hex.toHexString(bytes);
    }
    public byte[] hex2bytes(String hex){
        return Hex.decode(hex);
    }
}