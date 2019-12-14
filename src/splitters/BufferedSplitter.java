package splitters;

import java.io.*;

import static utils.Const.DIM_MAX_BUF;
import static utils.Const.DIM_MAX_PAR;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer.
 */
public class BufferedSplitter extends Splitter implements Runnable {

    private int dimPar;
    private long nParti;
    private boolean parti;

    public BufferedSplitter(String path, int dimPar){
        super(path);
        this.dimPar = dimPar;
        nParti = -1;
        parti = false;
    }
    public BufferedSplitter(String path, long numPar){
        super(path);
        File tmp = new File(path);
        this.nParti = numPar;
        this.dimPar = (int)((tmp.length()/nParti)+(tmp.length()%nParti));

        parti = true;
    }


    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     */
    public BufferedSplitter(String path){
        super(path);
    }

    public BufferedSplitter(File f, int dimPar){
        super(f);
        this.dimPar = dimPar;
    }
    public BufferedSplitter(File f, long numPar){
        super(f);
        this.dimPar = (int)((f.length()/numPar)+(f.length()%numPar));
    }

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public BufferedSplitter(File f){
        super(f);
    }

    public int getDimPar() {
        return dimPar;
    }

    public void setDimPar(int dimPar) {
        this.dimPar = dimPar;
    }

    /**
     * Metodo che sovrascrive quello implementato dall'interfaccia Runnable.
     * Chiama il metodo split().
     */
    @Override
    public void run() {
        if(startFile.getName().indexOf(".par") == -1)
            split();
        else
            merge();
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
            fos = new FileOutputStream(startFile.getName()+""+"1.par");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) startFile.length(), c = 1, length = 0, dimBuf = DIM_MAX_BUF;
        byte[] buf = new byte[dimBuf];

        try {
            while ((length = fis.read(buf, 0, buf.length)) >= 0){
                if((dimPar-length) >= 0) {                  //se lo spazio non è ancora finito scrivo normalmente
                    fos.write(buf, 0, length);
                    dimPar -= length;
                }
                else{
                    //se lo spazio è finito devo svuotare il buffer finché può e finire di svuotarlo nel nuovo stream
                    int rem = length-dimPar;
                    fos.write(buf, 0, dimPar);
                    fos.close();
                    fos = new FileOutputStream(startFile.getName() + "" + (++c) + ".par");
                    fos.write(buf, dimPar, rem);
                    dimPar = DIM_MAX_PAR-rem;         //reimposto la dimensione della partizione tenendo conto di quello che ho già scritto
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
    }

    /**
     * Metodo che implementa la ricomposizione semplice delle parti di un file.
     */
    public void merge() {
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

    public boolean isParti() {
        return parti;
    }

    public long getnParti() {
        return nParti;
    }
}



        /*while(true){
            try {
                if (!(fis.read(buf) != -1)) break; //se la lettura è finita esco dal ciclo
                //trasf -= dimBuf;
                fos.write(buf);                 //scrittura del buffer letto in precedenza
                dimPar -= dimBuf;               //tolgo alla dimensione della partizione la dimensione del buffer
                if(/*trasf <= 0 || dimPar <= 0) {
                    dimPar = 2097152;         //reimposto la dimensione della partizione
                    fos.flush();                //svuoto lo stream
                    fos.close();                //chiudo lo stream e ne creo uno nuovo
                    fos = new FileOutputStream(startFile.getName() + "" + (++c) + ".par");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/