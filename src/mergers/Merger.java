package mergers;

import static utils.Const.DIM_MAX_BUF;
import static utils.MyUtils.MD5;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Classe che implementa tutti i meccanismi di merging dei file divisi a seconda di come sono stati inizialmente separati.
 */
public class Merger {
    /**
     * Il file da cui iniziare a ricostruire quello originale.
     */
    private File startFile;

    /**
     * Costruttore per inizializzare il file da cui partire.
     * @param path Path per il primo file da ricostruire.
     */
    public Merger(String path) {
        startFile = new File(path);
    }

    /**
     * Costruttore per inizializzare il file da cui partire.
     * @param f Primo file da cui far partire la ricostruzione.
     */
    public Merger(File f) {
        startFile = f;
    }

    /**
     * Metodo principale che in base all'estensione del file iniziale capisce se deve decriptare, decomprimere oppure effettuare una semplice unione.
     */
    public void merge() {
        String ext = startFile.getName().substring(startFile.getName().lastIndexOf(".par") + (".par".length()), (int) startFile.getName().length());
        switch (ext) {
            case ".crypto":
                decript();
                break;
            case ".zip":
                unzip();
                break;
            default:
                simpleMerge();
                break;
        }
    }

    /**
     * Metodo privato, chiamato da merge() nel caso in cui il file da ricomporre sia compresso.
     */
    private void unzip() {
        //nome del file originale per cercare le parti successive
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par") - 1);

        //nome del file ricostruito
        String nomeFileFinale = nomeFile + "fine";

        int c = 1, dimBuf = DIM_MAX_BUF;
        byte[] buf = new byte[dimBuf];

        //creo il file che verrà ricostruito
        File out = new File(nomeFileFinale);

        ZipInputStream zis = null;
        FileOutputStream fos = null;

        try {
            //apro gli stream di lettura di zip e scrittura del file finale
            zis = new ZipInputStream(new FileInputStream(startFile.getName()));

            if (!out.exists())
                out.createNewFile();
            fos = new FileOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = 0;                 //quanti byte vengono letti in ogni ciclo
        File attuale = startFile;       //file attuale da cui iniziare a leggere
        while (attuale.exists()) {      //finché non ha ancora letto l'ultima parte
            try {
                zis.getNextEntry();     //ottengo la prima ZipEntry
                while ((length = zis.read(buf, 0, buf.length)) >= 0) {
                    fos.write(buf, 0, length);                     //scrivi
                }
            } catch (IOException e) {
                    e.printStackTrace();
            }
            c++;                    //aggiorno il file da cui andrò a leggere
            attuale = new File(nomeFile + (c) + ".par.zip"); //cambia l'input da cui leggere
            try {
                //se le parti non sono finite
                if (attuale.exists()) {
                    zis.close();
                    zis = new ZipInputStream(new FileInputStream(attuale));     //crea il nuovo stream
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            zis.close();                //chiudi gli stream
            fos.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Metodo privato, chiamato da merge() nel caso in cui il file da ricomporre sia criptato con una password da utente.
     */
    private void decript() {
        System.out.println("Inserisci la chiave per decriptare");
        String chiaveString = null;
        byte[] digestedPass;                //inserimento della password per decriptare
        try {
            chiaveString = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        digestedPass = MD5(chiaveString);
        Key key = new SecretKeySpec(digestedPass, 0, digestedPass.length, "AES");
        //creo la chiave
        byte[] iv = new byte[16];
        File attuale = startFile;
        FileInputStream fis = null;

        //apro lo stream in chiaro per poter leggere l'IV
        try{
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        //nome del file originale
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1);
        int dimBuf = DIM_MAX_BUF, c = 1;
        byte[] buf = new byte[dimBuf];

        CipherInputStream cis = null;
        FileOutputStream fos = null;
        try {
            //apro gli stream
            cis = new CipherInputStream(fis, cipher);
            fos = new FileOutputStream(new File(startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1) + "fine"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(attuale.exists()) {
            try {
                //ciclo di lettura e decifratura
                int length = 0;
                while ((length = cis.read(buf, 0 , buf.length)) >= 0)
                    fos.write(buf, 0, length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //prossima parte da leggere
            attuale = new File(nomeFile + (++c) + ".par.crypto");
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
        try{
            cis.close();            //chiudo gli stream
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo privato, chiamato da merge() nel caso in cui il file sia solo da ricomporre.
     */
    private void simpleMerge() {
        //nome del file originale
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1);
        //nome del file ricostruito
        String nomeFileFinale = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1) + "fine";

        int c = 1, dimBuf = DIM_MAX_BUF;
        byte[] buf = new byte[dimBuf];

        FileOutputStream output = null;
        File attuale = startFile, out = new File(nomeFileFinale);
        FileInputStream fis = null;
        try {
            //apertura degli stream
            if (!out.exists())
                out.createNewFile();
            output = new FileOutputStream(out);
            fis = new FileInputStream(attuale);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = 0;

        //se la parte attuale esiste
        while(attuale.exists()){
            try {
                //ciclo di lettura
                while((length = fis.read(buf, 0, buf.length)) >= 0)
                    output.write(buf, 0, length);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            //controllo se c'è un'altra parte
            attuale = new File(nomeFile+(++c)+".par");
            try {
                //se non sono finite le parti
                if(attuale.exists()) {
                    fis.close();
                    fis = new FileInputStream(attuale);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();        //chiudo gli stream
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




//        File attuale = startFile;
//        int c = 1;
//        while(attuale.exists()){
//            try {
//                cipher.doFinal(Files.readAllBytes(attuale.toPath()));
//            } catch (IllegalBlockSizeException e) {
//                e.printStackTrace();
//            } catch (BadPaddingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            attuale.renameTo(new File(attuale.getName().substring(0, attuale.getName().lastIndexOf(".par")+".par".length())));
//            attuale = new File(attuale.getName().substring(0, (attuale.getName().lastIndexOf(".par")-(String.valueOf(c).length())))+(++c)+".par.crypto");
//        }