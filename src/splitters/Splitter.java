package splitters;

import java.io.File;

/**
 * Classe astratta da cui derivano tutti gli Splitter.
 * Qui dentro è contenuto il file da dividire nei diversi modi.
 * @see Runnable
 */
public abstract class Splitter implements Runnable{

    /**
     * Il file verrà effettuata la divisione o l'unione.
     */
    protected File startFile;

    /**
     * Progresso nella divisione del file.
     */
    protected double progress = 0;

    /**
     * Attributo per controllare se la divisione del file è finita o meno.
     */
    protected boolean finished = false;

    /**
     * Valore booleano per capire che cosa fare col file, se dividerlo o unirlo alle altre parti.
     */
    protected boolean split = true;

    /**
     * Directory in cui andranno le parti del file diviso.
     */
    protected String finalDirectory = "";

    /**
     * Costruttore per inizializzare il file.
     * @param f File su cui lavorare.
     * @param split true se il file è da dividere, false se è da unire.
     * @param dir Directory dove andranno i file divisi, "" se è da unire.
     */
    public Splitter(File f, boolean split, String dir){
        startFile = f;
        this.split = split;
        this.finalDirectory = dir;
    }

    /**
     * Metodo che ritorna il numero di byte letti finora durante la divisione.
     * @return Numero di byte letti.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Metodo che setta il valore del progresso nella divisione del file.
     * @param progress Valore attuale del progresso.
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }

    /**
     * Metodo per ottenere il file su cui lavorare.
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
     * @param finalDirectory Directory in cui mettere i file divisi.
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
     * Ogni Splitter dovrà implementare questo metodo per effettuare la divisione del file.
     */
    abstract void split();

    /**
     * Ogni Splitter dovrà implementare questo metodo per effettuare la ricostruzione delle parti.
     */
    abstract void merge();

    /**
     * Metodo che sovrascrive quello implementato dall'interfaccia {@link Runnable Runnable}.
     * A seconda del valore dell'attributo split verrà eseguita la divisione o l'unione.
     */
    @Override
    public void run(){
        if (split)
            split();
        else
            merge();
    }
}