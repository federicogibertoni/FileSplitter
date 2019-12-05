package splitters;

import java.io.*;
import java.util.Dictionary;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static utils.Const.DIM_MAX_BUF;
import static utils.Const.DIM_MAX_PAR;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer e comprime tutte le partizioni create.
 */
public class ZipSplitter extends Splitter implements Runnable {
    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public ZipSplitter(File f){
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
        split();
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer.
     * I file creati vengono tutti compressi tramite l'utilizzo di un ZipOutputStream.
     */
    public void split() {
        assert startFile.exists();              //controllo che il file esista altrimenti termino l'esecuzione

        String outputFile = startFile.getName()+"1.par";    //nome della prima ZipEntry

        int trasf = (int) startFile.length(), c = 1, i = 0, dimBuf = DIM_MAX_BUF, dimPar = DIM_MAX_PAR;

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
                }
                else {
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    zos.write(buf, 0, dimPar);
                    zos.closeEntry();               //chiudo la entry attuale visto che è finita
                    zos.close();
                    zos = new ZipOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.zip"));
                    //apro una nuova entry
                    zos.putNextEntry(new ZipEntry(startFile.getName() + "" + (c) + ".par"));
                    zos.write(buf, dimPar, rem);
                    dimPar = DIM_MAX_PAR-rem;
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