package utils;

/**
 * Classe contenente tutte le costanti utilizzate nel progetto.
 */
public class Const {
    /**
     * Massima dimensione del buffer di lettura.
     */
    public static final int DIM_MAX_BUF = 8 * 1024;

    /**
     * Costante che rappresenta il valore della colonna in cui è contenuta la JProgressBar.
     */
    public static final int PROGRESS_BAR_COLUMN = 4;

    /**
     * Stringa di errore mostrata nel caso non siano riempiti i campi necessari nel JDialog.
     */
    public static final String FIELD_ERROR_MESSAGE = "I campi adeguati non sono stati riempiti!\nIl file non sarà messo in coda.";

    /**
     * Titolo del JOptionPane che mostra l'errore nell'inserimento dei dati nei campi.
     */
    public static final String TITLE_FIELD_ERROR_MESSAGE = "Errore nell'input";
}
