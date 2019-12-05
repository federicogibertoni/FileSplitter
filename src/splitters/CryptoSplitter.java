package splitters;

import static utils.Const.DIM_MAX_BUF;
import static utils.Const.DIM_MAX_PAR;
import static utils.MyUtils.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

import java.security.*;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e li cripta con una chiave chiesta all'utente.
 */
public class CryptoSplitter extends Splitter implements Runnable {
    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     */
    public CryptoSplitter(String path){
        super(path);
    }

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public CryptoSplitter(File f){
        super(f);
    }

    /**
     * Metodo che sovrascrive quello implementato dall'interfaccia Runnable.
     * Chiama il metodo split().
     */
    @Override
    public void run() {
        split();
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer
     * e scrive i file divisi criptati con una password chiesta da utente.
     * L'IV usato nel cipher è scritto nella prima parte del primo file, in chiaro.
     */
    void split() {
        assert startFile.exists();               //controllo che il file esista altrimenti termino l'esecuzione

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Key key = null;

        System.out.println("Inserisci una password per criptare");
        String pass = null;                 //chiedo una password all'utente
        byte[] digestedPass;
        try {
            //faccio inserire all'utente una password per criptare
            pass = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        digestedPass = MD5(pass);           //faccio l'hash a 128 bit della password dell'utente
        key = new SecretKeySpec(digestedPass,0,digestedPass.length, "AES");     //creo una chiave

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
            fos = new FileOutputStream(startFile.getName()+""+"1.par.crypto");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) startFile.length(), c = 1, i = 0, dimBuf = DIM_MAX_BUF, dimPar = DIM_MAX_PAR;
        byte[] buf = new byte[dimBuf];
        try {
            fos.write(iv);              //scrivo l'IV all'inizio del file per salvarlo
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
                }
                else{
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    cos.write(buf, 0, dimPar);
                    cos.close();
                    cos = new CipherOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.crypto"), cipher);
                    cos.write(buf, dimPar, rem);
                    dimPar = DIM_MAX_PAR - rem;
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
}

                /*
                else{
                    int rem = length-dimPar;
                    fos.write(buf, 0, dimPar);
                    fos.close();
                    fos = new FileOutputStream(startFile.getName() + "" + (++c) + ".par");
                    fos.write(buf, dimPar, rem);
                    dimPar = DIM_MAX_PAR-rem;         //reimposto la dimensione della partizione
                }
            }*/

                /*if (!(fis.read(buf) != -1)) break;          //leggo dallo stream in chiaro
                //trasf -= dimBuf;
                cos.write(buf);                     //scrivo il buffer con lo stream cifrato
                dimPar -= dimBuf;                   //sottraggo alla dimensione della partizione quella del buffer
                if(/*trasf <= 0 || dimPar <= 0) { //quando la partizione è piena
                    dimPar = 104857600;             //reimposto la dimensione
                    cos.flush();                    //svuoto e chiudo lo stream
                    cos.close();
                    //creo un nuovo stream per una nuova partizione
                    cos = new CipherOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.crypto"), cipher);
                }*/
/*Thread splitter = new Thread(split);
        splitter.start();
        try {
            splitter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File attuale = new File(file.getName()+"1.par");
        int c = 1;

        while(attuale.exists()){
            KeyGenerator keygen = null;
            try {
                keygen = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            keygen.init(128);

            Key key = keygen.generateKey();

            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                cipher.doFinal(Files.readAllBytes(attuale.toPath()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            attuale.renameTo(new File(attuale.getName()+".crypto"));
            attuale = new File(attuale.getName().substring(0, attuale.getName().lastIndexOf(".par")-1)+(++c)+".par");
        }*/