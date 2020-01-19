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
     * Progresso nella divisione del file.
     */
    protected double progress = 0;

    /**
     * Metodo che ritorna il numero di byte letti finora.
     * @return Numero di byte letti.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Metodo che setta il valore del progresso nella divisione di un file.
     * @param progress Valore attuale del progresso.
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }

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

    /**
     * Ogni Splitter dovrà implementare questo metodo per effettuare la ricostruzione.
     */
    abstract void merge();

    /**
     * Ogni Splitter dovrà implementarlo per poter implementare l'interfaccia Runnable.
     */
    @Override
    public abstract void run();
}
