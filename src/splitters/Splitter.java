package splitters;

import java.io.File;

/**
 * Classe astratta da cui derivano tutti gli Splitter.
 * Qui dentro è contenuto il file da cui inizierà lo split nelle varie istanze.
 */
public abstract class Splitter implements Runnable{

    /**
     * Il file da dividere.
     */
    protected File startFile;

    /**
     * Costruttore per inizializzare il file da dividere.
     * @param f File da dividere.
     */
    public Splitter(File f){
        startFile = f;
    }

    /**
     * Costruttore per inizializzare il file da dividere.
     * @param path Path del file da dividere.
     */
    public Splitter(String path){
        startFile = new File(path);
    }

    /**
     * Metodo per ottenere il file da dividere.
     * @return File da dividere.
     */
    public File getStartFile() {
        return startFile;
    }

    /**
     * Cambiare il file da dividere
     * @param startFile Nuovo File da dividere.
     */
    public void setStartFile(File startFile) {
        this.startFile = startFile;
    }

    /**
     * Ogni Splitter dovrà implementare questo metodo per effettuare la divisione.
     */
    abstract void split();
}
