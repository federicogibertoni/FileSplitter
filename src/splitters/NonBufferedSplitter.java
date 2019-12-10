package splitters;

import java.io.*;

/**
 * Classe che implementa lo splitter senza buffer, legge un byte per ogni ciclo.
 */
public class NonBufferedSplitter implements Runnable{
    /**
     * Attributo che contiene il file da dividere.
     */
    private File startFile;

    /**
     * Costruttore dello Splitter.
     * @param path Path del file da dividere.
     */
    public NonBufferedSplitter(String path){
//        super(path);
        startFile = new File(path);
    }

    /**
     * Costruttore dello Splitter.
     * @param f File da dividere.
     */
    public NonBufferedSplitter(File f){
//        super(f);
        startFile = f;
    }

    /**
     * Metodo che sovrascrive la sua implementazione in Runnable.
     * Usato per dividere i file in parti di dimensioni uguale, fatta eccezione per l'ultima.
     */
    public void split() {
        int dim = 10240;                //dimensione massima di una parte
        assert startFile.exists();           //controllo che il file esista, altrimenti termino l'esecuzione

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(startFile);    //creo un nuovo stream di output
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int trasf, c = 0;
        for(int i = 0; i<startFile.length(); i++){
            try {
                if(i == 0 || i % dim == 0) {  //se lo stream è appena iniziato oppure è finita la partizione ne creo un altro
                    if(fos != null)
                        fos.close();
                    fos = new FileOutputStream(startFile.getName() + "" + (++c) + ".par");
                }
                trasf = fis.read();     //leggo un intero
                fos.write(trasf);       //scrivo un intero
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();                //chiudo gli stream
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        split();
    }
}
