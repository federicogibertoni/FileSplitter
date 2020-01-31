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

    /**
     * Metodo per inserire una stringa dentro ad un'altra in una certa posizione.
     * Usato per inserire le estensioni nei file ricostruiti.
     * @param originalString La stringa originale che sarà modificata.
     * @param stringToBeInserted La stringa che dovrà essere inserita nell'originale.
     * @param index La posizione a cui inserire la stringa.
     * @return La stringa completa, con il nuovo pezzo inserito nella posizione specificata.
     */
    public static String insertString(String originalString, String stringToBeInserted, int index) {
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {
            //copio ogni carattere nella stringa da restituire
            newString += originalString.charAt(i);

            //quando raggiungo l'indice giusto allora inserisco la stringa
            if (i == index)
                newString += stringToBeInserted;
        }
        return newString;
    }
}