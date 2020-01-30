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
     * Costante utilizzata in tutte le divisioni per aggiungere l'estensione ".par" alla fine dei file.
     */
    public static final String SPLIT_EXTENSION = ".par";

    /**
     * Costante utilizzata nelle divisioni con cifratura del contenuto.
     */
    public static final String CRYPT_EXTENSION = ".crypto";

    /**
     * Costante utilizzata nelle divisioni con compressione del contenuto.
     */
    public static final String ZIP_EXTENSION = ".zip";

    /**
     * Costante utilizzata in fase di riunione delle parti divise.
     */
    public static final String MERGE_EXTENSION = "_merge";

    /**
     * Stringa di errore mostrata nel caso non siano riempiti i campi necessari nel JDialog.
     */
    public static final String FIELD_ERROR_MESSAGE = "I campi adeguati non sono stati riempiti!";

    /**
     * Titolo del JOptionPane che mostra l'errore nell'inserimento dei dati nei campi.
     */
    public static final String TITLE_FIELD_ERROR_MESSAGE = "Errore nell'input";

    /**
     * Stringa per avvisare che è finita l'unione del file selezionato.
     */
    public static final String FINISHED_MERGE_MESSAGE = "Finita l'unione del file.";
}
