package splitters;

import utils.MyUtils;

import javax.swing.*;
import java.io.*;

import static utils.Const.*;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer.
 */
public class BufferedSplitter extends Splitter implements Runnable {

    /**
     * Intero contenente la dimensione di ogni parte in cui sarà diviso il file iniziale.
     */
    private int dimPar;

    /**
     * Long contenente il numero di parti uguali in cui dividere il file.
     */
    private long nParti;

    /**
     * Booleano per capire se il file è da dividere in n parti o c'è una dimensione massima.
     */
    private boolean parti;

    /**
     * Costruttore dello splitter nel caso in cui sia specificata una dimensione massima dei file. Chiamato in fase di divisone alla chiusura del SettingsDialog.
     * @param f File da cui iniziare.
     * @param split true se il file è da dividere, false se è da unire.
     * @param dimPar Dimensione di ogni parte.
     * @param dir Directory dove andranno le parti del file diviso.
     */
    public BufferedSplitter(File f, boolean split, int dimPar, String dir) {
        super(f, split, dir);
        this.dimPar = dimPar;
        this.parti = true;
    }

    /**
     * Costruttore dello splitter nel caso in cui sia specificato il numero massimo di file da ottenere. Chiamato in fase di divisone alla chiusura del SettingsDialog.
     * @param f File da cui iniziare.
     * @param split true se il file è da dividere, false se è da unire.
     * @param numPar Numero massimo di file da generare.
     * @param dir Directory dove andranno le parti del file diviso.
     */
    public BufferedSplitter(File f, boolean split, long numPar, String dir){
        super(f, split, dir);
        this.nParti = numPar;
        this.dimPar = (int)((f.length()/numPar)+(f.length()%numPar));
        this.parti = false;
    }

    /**
     * Costruttore dello Splitter, chiamato in fase di unione delle parti.
     * @param f File di partenza.
     * @param split true se il file è da dividere, false se è da unire.
     */
    public BufferedSplitter(File f, boolean split){
        super(f, split, "");
    }

    /**
     * Metodo per ottenere la dimensione di ogni parte in cui sarà diviso il file iniziale.
     * @return La dimensione di ogni parte.
     */
    public int getDimPar() {
        return dimPar;
    }

    /**
     * Metodo per capire se un file è del tipo di divisione in parti uguali o dimensioni massime.
     * @return true se il file è da dividere il n parti uguali, false altrimenti.
     */
    public boolean isParti() {
        return parti;
    }

    /**
     * Metodo per ottenre il numero di parti in cui dividere il file.
     * @return Numero di parti in cui il file va diviso.
     */
    public long getnParti() {
        return nParti;
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer.
     */
    public void split() {
        assert startFile.exists();           //controllo che il file esista, altrimenti termino l'esecuzione

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(startFile);                //creo gli stream di lettura e scrittura
            fos = new FileOutputStream(finalDirectory+File.separator+startFile.getName()+""+"1"+SPLIT_EXTENSION);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int c = 1, length = 0, dimBuf = DIM_MAX_BUF, dimParTmp = dimPar;
        byte[] buf = new byte[dimBuf];

        try {
            while ((length = fis.read(buf, 0, buf.length)) >= 0){
                if((dimPar-length) >= 0) {                  //se lo spazio non è ancora finito scrivo normalmente
                    fos.write(buf, 0, length);
                    dimPar -= length;

                    progress += length;
                }
                else{
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    fos.write(buf, 0, dimPar);
                    progress += dimPar;
                    fos.close();
                    fos = new FileOutputStream(finalDirectory+File.separator+startFile.getName() + "" + (++c) + SPLIT_EXTENSION);
                    fos.write(buf, dimPar, rem);
                    dimPar = dimParTmp-rem;         //reimposto la dimensione della partizione tenendo conto di quello che ho già scritto

                    progress += rem;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fis.close();                //chiudo tutti gli stream
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    /**
     * Metodo che implementa la ricomposizione semplice delle parti di un file.
     */
    public void merge() {
        //nome del file originale
        String nomeFile = null;
        try {
            nomeFile = startFile.getCanonicalPath().substring(0, startFile.getCanonicalPath().lastIndexOf(SPLIT_EXTENSION)-1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //nome del file ricostruito
        String nomeFileFinale = MyUtils.insertString(nomeFile, MERGE_EXTENSION, nomeFile.lastIndexOf(".")-1);

        int c = 1, dimBuf = DIM_MAX_BUF;
        byte[] buf = new byte[dimBuf];

        FileOutputStream output = null;
        File attuale = startFile;
        FileInputStream fis = null;
        try {
            //apertura degli stream
            output = new FileOutputStream(nomeFileFinale);
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
            attuale = new File(nomeFile+(++c)+SPLIT_EXTENSION);
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
        finished = true;
        JOptionPane.showMessageDialog(null, FINISHED_MERGE_MESSAGE);
    }
}