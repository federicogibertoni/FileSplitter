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
     * Attributo per controllare se la divisione di un file è finita o meno.
     */
    protected boolean finished = false;

    /**
     * Valore booleano per capire che azione c'è da fare, se split o merge.
     */
    protected boolean split = true;

    /**
     * Directory in cui andranno le parti del file diviso.
     */
    protected String finalDirectory = "";

    /**
     * Costruttore per inizializzare il file da dividere.
     * @param f File da dividere.
     * @param split true se il file è da dividere, false se è da unire.
     */
    public Splitter(File f, boolean split, String dir){
        startFile = f;
        this.split = split;
        this.finalDirectory = dir;
    }

    /**
     * Costruttore per inizializzare il file da dividere.
     * @param path Path del file da dividere.
     * @param split true se il file è da dividere, false se è da unire.
     */
    public Splitter(String path, boolean split){
        startFile = new File(path);
        this.split = split;
    }

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
     * Metodo per ottenere il file da dividere.
     * @return File da dividere.
     */
    public File getStartFile() {
        return startFile;
    }

    /**
     * Metodo per ottenere la directory dove andrà il file diviso.
     * @return Directory di dove saranno le parti finali.
     */
    public String getFinalDirectory() {
        return finalDirectory;
    }

    /**
     * Metodo per impostare la directory dove andrà il file diviso.
     */
    public void setFinalDirectory(String finalDirectory) {
        this.finalDirectory = finalDirectory;
    }

    /**
     * Metodo per controllare lo stato della divisione, se è finita o meno.
     * @return true se la divisione è finita, false altrimenti.
     */
    public boolean isFinished() {
        return finished;
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
     * Metodo che sovrascrive quello implementato dall'interfaccia Runnable.
     * A seconda del valore dell'attributo split verrà eseguita la divisione o l'unione
     */
    @Override
    public void run(){
        if (split)
            split();
        else
            merge();
    }
}