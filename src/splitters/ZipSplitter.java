package splitters;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static utils.Const.DIM_MAX_BUF;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e comprime tutte le partizioni create.
 */
public class ZipSplitter extends Splitter implements Runnable {

    /**
     * Intero contenente la dimensione di ogni parte in cui sarà diviso il file iniziale.
     */
    private int dimPar;

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     * @param dimPar Dimensione di ogni parte.
     */
    public ZipSplitter(File f, int dimPar){
        super(f);
        this.dimPar = dimPar;
    }

    /**
     * Ritorna la dimensione di ogni parte dello split.
     * @return Dimensione, uguale per ogni file.
     */
    public int getDimPar() {
        return dimPar;
    }

    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     * @param dimPar Dimensione di ogni parte.
     */
    public ZipSplitter(String path, int dimPar){
        super(path);
        this.dimPar = dimPar;
    }

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public ZipSplitter(File f) {
        super(f);
    }

    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     */
    public ZipSplitter(String path){
        super(path);
    }

    /**
     * Metodo che sovrascrive quello implementato dall'interfaccia Runnable.
     * Chiama il metodo split().
     */
    @Override
    public void run() {
        if(startFile.getName().indexOf(".par.zip") == -1)
            split();
        else
            merge();
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer.
     * I file creati vengono tutti compressi tramite l'utilizzo di un ZipOutputStream.
     */
    public void split() {
        assert startFile.exists();              //controllo che il file esista altrimenti termino l'esecuzione

        String outputFile = startFile.getName()+"1.par";    //nome della prima ZipEntry

        int c = 1, dimBuf = DIM_MAX_BUF, dimParTmp = dimPar;

        byte[] buf = new byte[dimBuf];

        FileInputStream fis = null;
        ZipOutputStream zos = null;

        try{
            fis = new FileInputStream(startFile);       //creo gli stream
            zos = new ZipOutputStream(new FileOutputStream(outputFile+".zip"));

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
                    zos = new ZipOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.zip"));
                    //apro una nuova entry
                    zos.putNextEntry(new ZipEntry(startFile.getName() + "" + (c) + ".par"));
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
}

/*while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                //trasf -= dimBuf;
                fos.write(buf);
                dimPar -= dimBuf;
                if(/*trasf <= 0 || dimPar <= 0) {
                    dimPar = 104857600;
                    fos.close();
                    fos = new FileOutputStream(file.getName() + "" + (++c) + ".par");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

                /*while ((length = fis.read(buf, 0, buf.length)) >= 0){
                if((dimPar-length) >= 0) {
                    fos.write(buf, 0, length);
                    dimPar -= length;
                }
                else{
                    int rem = length-dimPar;
                    fos.write(buf, 0, dimPar);
                    fos.close();
                    fos = new FileOutputStream(startFile.getName() + "" + (++c) + ".par");
                    fos.write(buf, dimPar, rem);
                    dimPar = DIM_MAX_PAR-rem;         //reimposto la dimensione della partizione
                }
            }*/

                /*//leggo dallo stream
                if (fis.read(buf, 0, buf.length) == -1) break;
                zos.write(buf);             //scrivo nel file compresso
                dimPar -= dimBuf;           //tolgo alla dimensione della partizione la dimensione del buffer
                if(dimPar <= 0){
                    dimPar = 104857600;       //reimposto la dimensione della partizione
                    zos.flush();
                    zos.closeEntry();       //chiudo la entry, svuoto lo stream e lo chiudo
                    zos.close();
                    //creo un nuovo stream e creo la prima entry
                    zos = new ZipOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.zip"));
                    zos.putNextEntry(new ZipEntry(startFile.getName() + "" + (c) + ".par"));
                }*/