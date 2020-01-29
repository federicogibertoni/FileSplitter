package splitters;

import utils.MyUtils;

import javax.swing.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.Const.*;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e comprime tutte le partizioni create.
 */
public class ZipSplitter extends Splitter implements Runnable {

    /**
     * Intero contenente la dimensione di ogni parte in cui sarà diviso il file iniziale.
     */
    private int dimPar;

    /**
     * Costruttore dello Splitter. Chiamato in fase di divisione.
     * @param f File da dividere.
     * @param split true se il file è da dividere, false se è da unire.
     * @param dimPar Dimensione di ogni parte.
     * @param dir Directory dove andranno le parti del file diviso.
     */
    public ZipSplitter(File f, boolean split, int dimPar, String dir){
        super(f, split, "");
        this.dimPar = dimPar;
        this.finalDirectory = dir;
    }

    /**
     * Costruttore dello Splitter. Chiamato in fase di unione.
     * @param f File da dividere.
     * @param split true se il file è da dividere, false se è da unire.
     */
    public ZipSplitter(File f, boolean split) {
        super(f, split, "");
    }

    /**
     * Ritorna la dimensione di ogni parte dello split.
     * @return Dimensione, uguale per ogni file.
     */
    public int getDimPar() {
        return dimPar;
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer.
     * I file creati vengono tutti compressi tramite l'utilizzo di un ZipOutputStream.
     */
    public void split() {
        assert startFile.exists();              //controllo che il file esista altrimenti termino l'esecuzione

        String outputFile = startFile.getName()+"1"+SPLIT_EXTENSION;    //nome della prima ZipEntry

        int c = 1, dimBuf = DIM_MAX_BUF, dimParTmp = dimPar;

        byte[] buf = new byte[dimBuf];

        FileInputStream fis = null;
        ZipOutputStream zos = null;

        try{
            fis = new FileInputStream(startFile);       //creo gli stream
            zos = new ZipOutputStream(new FileOutputStream(finalDirectory+File.separator+outputFile+ZIP_EXTENSION));

            zos.putNextEntry(new ZipEntry(outputFile));     //inserisco la prima entry nel primo file
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = 0;
        try {
            while ((length = fis.read(buf, 0, buf.length)) >= 0){
                if((dimPar-length) >= 0) {          //se lo spazio non è ancora finito scrivo normalmente
                    zos.write(buf, 0, length);
                    dimPar -= length;

                    progress += length;
                }
                else {
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    zos.write(buf, 0, dimPar);
                    progress += dimPar;
                    zos.closeEntry();               //chiudo la entry attuale visto che è finita
                    zos.close();
                    zos = new ZipOutputStream(new FileOutputStream(finalDirectory+File.separator+startFile.getName() + "" + (++c) + SPLIT_EXTENSION+ZIP_EXTENSION));
                    //apro una nuova entry
                    zos.putNextEntry(new ZipEntry(startFile.getName() + "" + (c) + SPLIT_EXTENSION));
                    zos.write(buf, dimPar, rem);
                    dimPar = dimParTmp-rem;

                    progress += rem;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            fis.close();            //chiudo tutti gli stream e l'ultima entry aperta
            zos.flush();
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finished = true;
    }

    /**
     * Metodo che implementa la ricostruzione del file originale decomprimento le parti in cui esso è stato diviso.
     */
    public void merge(){
        //nome del file originale per cercare le parti successive
        String nomeFile = null;
        try {
            nomeFile = startFile.getCanonicalPath().substring(0, startFile.getCanonicalPath().lastIndexOf(SPLIT_EXTENSION) - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //nome del file ricostruito
        String nomeFileFinale = MyUtils.insertString(nomeFile, MERGE_EXTENSION, nomeFile.lastIndexOf(".")-1);;

        int c = 1, dimBuf = DIM_MAX_BUF;
        byte[] buf = new byte[dimBuf];

        //creo il file che verrà ricostruito
        File out = new File(nomeFileFinale);

        ZipInputStream zis = null;
        FileOutputStream fos = null;

        try {
            //apro gli stream di lettura di zip e scrittura del file finale
            zis = new ZipInputStream(new FileInputStream(startFile.getCanonicalPath()));

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
            attuale = new File(nomeFile + (c) + SPLIT_EXTENSION+ZIP_EXTENSION); //cambia l'input da cui leggere
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
        finished = true;
        JOptionPane.showMessageDialog(null, FINISHED_MERGE_MESSAGE);
    }
}