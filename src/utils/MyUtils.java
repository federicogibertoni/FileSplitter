package utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import static utils.Const.*;

public class MyUtils {
    public static void storeKey(Key key){
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JCEKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        System.out.println("inserisci una password");
        char[] pass = null;
        try {
            pass = new BufferedReader(new InputStreamReader(System.in)).readLine().toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(new FileInputStream(new File(KEY_STORE_LOCATION)), pass);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(pass);
        SecretKey mySecretKey = new SecretKeySpec(new String(key.getEncoded()).getBytes(), "AES");
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(mySecretKey);
        try {
            keyStore.setEntry("chiaveDecrypt", secretKeyEntry, protectionParam);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    /*https://www.tutorialspoint.com/java_cryptography/java_cryptography_retrieving_keys.htm*/
    public static Key getKey(String password, String name){
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JCEKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        System.out.println("inserisci la password per il keystore");
        char[] pass = null;
        try {
            pass = new BufferedReader(new InputStreamReader(System.in)).readLine().toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(new FileInputStream(new File(KEY_STORE_LOCATION)), password.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static byte[] MD5(String pass){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(pass.getBytes());
        return md.digest();

    }
}
