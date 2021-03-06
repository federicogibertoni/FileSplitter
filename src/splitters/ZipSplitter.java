package splitters;

import utils.MyUtils;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.Const.*;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e comprime tutte le parti create.
 * È sottoclasse di {@link Splitter Splitter}.
 * @see Runnable
 */
public class ZipSplitter extends Splitter implements Runnable {

    /**
     * Intero contenente la dimensione di ogni parte in cui sarà diviso il file iniziale.
     */
    private int dimPar;

    /**
     * Costruttore dello Splitter.
     * Chiamato in fase di divisione alla chiusura del {@link gui.SettingsDialog SettingDialog}.
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
     * Costruttore dello Splitter.
     * Chiamato in fase di unione da UnioneActionListener, contenuto in {@link gui.MainPanel MainPanel}.
     * @param f File da dividere.
     * @param split true se il file è da dividere, false se è da unire.
     */
    public ZipSplitter(File f, boolean split) {
        super(f, split, "");
    }

    /**
     * Ritorna la dimensione di ogni parte dei file divisi.
     * @return La dimensione di ogni parte.
     */
    public int getDimPar() {
        return dimPar;
    }

    /**
     * Metodo che implementa la divisione di file tramite un buffer.
     * I file creati vengono tutti compressi tramite l'utilizzo di un ZipOutputStream.
     * @see ZipOutputStream
     */
    public void split() {
        assert startFile.exists();              //controllo che il file esista altrimenti termino l'esecuzione

        String outputFile = startFile.getName()+"1"+SPLIT_EXTENSION;    //nome della prima ZipEntry

        int c = 1, dimParTmp = dimPar;
        byte[] buf = new byte[DIM_MAX_BUF];

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
        String nomeFileFinale = MyUtils.insertString(nomeFile, MERGE_EXTENSION, nomeFile.lastIndexOf(".")-1);

        //comunico l'inizio
        JOptionPane.showMessageDialog(null, STARTED_MERGE_MESSAGE + nomeFileFinale);

        int c = 1;
        byte[] buf = new byte[DIM_MAX_BUF];

        //creo il file che verrà ricostruito
        File out = new File(nomeFileFinale);

        ZipInputStream zis = null;
        FileOutputStream fos = null;

        try {
            //apro gli stream di lettura di zip e scrittura del file finale
            zis = new ZipInputStream(new FileInputStream(startFile.getCanonicalPath()));
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
                    fos.write(buf, 0, length);              //scrivo nel file ricostruito
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
                        //comunico la fine
        JOptionPane.showMessageDialog(null, FINISHED_MERGE_MESSAGE+ nomeFileFinale);
    }
}