package utils;

import java.security.*;

/**
 * Classe che contiene alcune funzioni di utilità.
 */
public class MyUtils {
    /**
     * Metodo che restituisce l'MD5 di una stringa.
     * @param pass Stringa da cifrare.
     * @return I bytes dell'hash.
     */
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
