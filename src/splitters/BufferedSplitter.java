package splitters;

import java.io.*;

/**
 * Classe che implementa la divisione dei file tramite l'uso di un buffer.
 */
public class BufferedSplitter extends Splitter implements Runnable {
    /**
     * Attributo che contiene il file da dividere.
     */
    private File file;

    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     */
    public BufferedSplitter(String path){
        super(path);
        //file = new File(path);
    }

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public BufferedSplitter(File f){
        super(f);
        //file = f;
    }

    @Override
    public void run() {
        split();
    }

    /**
     * Metodo che implementa la divisione di file in dimensioni uguali(tranne l'ultimo) tramite un buffer.
     */
    public void split() {
        assert file.exists();           //controllo che il file esista, altrimenti termino l'esecuzione

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);                //creo gli stream di lettura e scrittura
            fos = new FileOutputStream(file.getName()+""+"1.par");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) file.length(), c = 1, dimBuf = 8192, dimPar = 104857600;
        byte[] buf = new byte[dimBuf];

        while(true){
            try {
                if (!(fis.read(buf) != -1)) break; //se la lettura è finita esco dal ciclo
                //trasf -= dimBuf;
                fos.write(buf);                 //scrittura del buffer letto in precedenza
                dimPar -= dimBuf;               //tolgo alla dimensione della partizione la dimensione del buffer
                if(/*trasf <= 0 || */dimPar <= 0) {
                    dimPar = 104857600;         //reimposto la dimensione della partizione
                    fos.flush();                //svuoto lo stream
                    fos.close();                //chiudo lo stream e ne creo uno nuovo
                    fos = new FileOutputStream(file.getName() + "" + (++c) + ".par");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();                //chiudo tutti gli stream
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
