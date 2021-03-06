package splitters;

import utils.MyUtils;

import static utils.Const.*;
import static utils.MyUtils.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e cripta le parti con una chiave inserita dall'utente.
 * È sottoclasse di {@link Splitter Splitter}.
 * @see Runnable
 */
public class CryptoSplitter extends Splitter implements Runnable {

    /**
     * Intero contenente la dimensione di ogni parte in cui sarà diviso il file iniziale.
     */
    private int dimPar;

    /**
     * La password con cui si fa la cifratura o decifratura.
     */
    private String pass;

    /**
     * Costruttore dello Splitter.
     * Chiamato in fase di divisione alla chiusura del {@link gui.SettingsDialog SettingDialog}.
     * @param f File da cui iniziare.
     * @param split true se il file è da dividere, false se è da unire.
     * @param pass Password con cui verrà gestito il file.
     * @param dimPar Dimensione di ogni parte.
     * @param dir Directory dove andranno le parti del file diviso.
     */
    public CryptoSplitter(File f, boolean split, String pass, int dimPar, String dir){
        super(f, split, dir);
        this.pass = pass;
        this.dimPar = dimPar;
    }

    /**
     * Costruttore dello Splitter.
     * Chiamato in fase di unione da UnioneActionListener, contenuto in {@link gui.MainPanel MainPanel}.
     * @param f File da cui iniziare.
     * @param split true se il file è da dividere, false se è da unire.
     * @param pass Password con cui verrà gestito il file.
     */
    public CryptoSplitter(File f, boolean split, String pass){
        super(f, split, "");
        this.pass = pass;
    }

    /**
     * Metodo per ottenere la dimensione di ogni parte dello split.
     * @return Dimensione di ogni parte, uguale per ogni file.
     */
    public int getDimPar() {
        return dimPar;
    }

    /**
     * Metodo che implementa la divisione del file tramite un buffer
     * e scrive le parti create criptandole con una password inserita da utente.
     * L'IV usato nel Cipher è scritto nella prima parte del primo file, in chiaro.
     * @see Cipher
     * @see IvParameterSpec
     * @see SecureRandom
     * @see Key
     */
    public void split() {
        assert startFile.exists();               //controllo che il file esista altrimenti termino l'esecuzione

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Key key = null;

        byte[] digestedPass = MD5(pass);           //faccio l'hash a 128 bit della password dell'utente
        key = new SecretKeySpec(digestedPass,0,digestedPass.length, "AES");     //creo una chiave per l'algoritmo AES

        SecureRandom srGen = new SecureRandom();
        byte[] iv = new byte[16];
        srGen.nextBytes(iv);        //genero in modo casuale un IV
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));     //creo un cipher con la chiave e l'IV
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        try {
            fis = new FileInputStream(startFile);                //apro gli stream in modalità "in chiaro"
            fos = new FileOutputStream(finalDirectory+File.separator+startFile.getName()+""+"1"+SPLIT_EXTENSION+CRYPT_EXTENSION);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int c = 1, dimParTmp = dimPar;
        byte[] buf = new byte[DIM_MAX_BUF];
        try {
            fos.write(iv);              //scrivo l'IV all'inizio del file per salvarlo e riprenderlo in unione
        } catch (IOException e) {
            e.printStackTrace();
        }
        //trasformo lo stream "in chiaro" in uno stream cifrato con il cipher creato prima
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int length = 0;
        try {
            while((length = fis.read(buf, 0, buf.length)) >= 0){
                if((dimPar-length) >= 0) {          //se lo spazio non è ancora finito scrivo normalmente
                    cos.write(buf, 0, length);
                    dimPar -= length;

                    progress += length;
                }
                else{
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    cos.write(buf, 0, dimPar);
                    progress += dimPar;
                    cos.close();
                    cos = new CipherOutputStream(new FileOutputStream(finalDirectory+File.separator+startFile.getName() + "" + (++c) + SPLIT_EXTENSION + CRYPT_EXTENSION), cipher);
                    cos.write(buf, dimPar, rem);
                    dimPar = dimParTmp - rem;

                    progress += rem;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();            //chiudo e svuoto tutti gli stream
            cos.flush();
            cos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che implementa la ricostruzione del file precedentemente criptato con password inserita da utente.
     */
    public void merge() {
        //nome del file originale
        String nomeFile = null;
        try {
            nomeFile = startFile.getCanonicalPath().substring(0, startFile.getCanonicalPath().lastIndexOf(SPLIT_EXTENSION) - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //nome del file ricostruito
        String nomeFileFinale = MyUtils.insertString(nomeFile, MERGE_EXTENSION, nomeFile.lastIndexOf(".")-1);;

        //comunico l'inizio
        JOptionPane.showMessageDialog(null, STARTED_MERGE_MESSAGE + nomeFileFinale);

        //genero la password criptata
        byte[] digestedPass = MD5(pass);
        Key key = new SecretKeySpec(digestedPass, 0, digestedPass.length, "AES");
        //creo la chiave
        byte[] iv = new byte[16];
        File attuale = startFile;
        FileInputStream fis = null;

        //apro lo stream in chiaro per poter leggere l'IV
        try {
            fis = new FileInputStream(attuale);
            fis.read(iv);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creo il cipher per decriptare con la chiave e l'IV
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        int c = 1;
        byte[] buf = new byte[DIM_MAX_BUF];


        CipherInputStream cis = null;
        FileOutputStream fos = null;
        try {
            //apro gli stream
            cis = new CipherInputStream(fis, cipher);
            fos = new FileOutputStream(new File(nomeFileFinale));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int length = 0;
        while (attuale.exists()) {
            try {
                //ciclo di lettura e decifratura
                while ((length = cis.read(buf, 0, buf.length)) >= 0)
                    fos.write(buf, 0, length);
            } catch (IOException e) {
                //do il messaggio d'errore
                JOptionPane.showMessageDialog(null, PASSWORD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);

                try {
                    cis.close();                    //chiudo gli stream
                    fos.close();

                    Files.deleteIfExists(Paths.get(nomeFileFinale));//cancello il nuovo file creato se la password è sbagliata
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            //prossima parte da leggere
            attuale = new File(nomeFile + (++c) + SPLIT_EXTENSION + CRYPT_EXTENSION);
            try {
                //se non sono finite le parti
                if (attuale.exists()) {
                    cis.close();
                    cis = new CipherInputStream(new FileInputStream(attuale), cipher);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            cis.close();            //chiudo gli stream
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //comunico la fine
        JOptionPane.showMessageDialog(null, FINISHED_MERGE_MESSAGE + nomeFileFinale);
    }
}