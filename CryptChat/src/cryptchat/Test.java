/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptchat;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 *
 * @author Vuk
 */
public class Test {

    public static void main(String[] args) {
        AsymmetricC ac = new AsymmetricC();
        ac.createNewKeys();
        String publicKey = ac.getPublicKeyHex();
        String ciphertext = ac.encryptMessage("Hello", publicKey);
        String decrypted = ac.decryptMessage(ciphertext);
        System.out.println(decrypted);
    }
}
