package dataAccessObjects;


import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author conme
 */
public class SecureHelper {
    private static final String ALGO = "AES";
    private static final byte[] KEY = 
        new byte[] { 
            'P','A','T','R','I','C','K',
            'A','A','R','N','E',
            'J','A','K','K'
        };

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY, ALGO);
        return key;
    }
    
//    public static void main (String[] args) {
//        try {
//            System.out.println(encrypt("32"));
//        } catch (Exception ex) {
//            Logger.getLogger(SecureHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
