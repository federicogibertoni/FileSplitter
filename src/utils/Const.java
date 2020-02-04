package utils;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.ZipSplitter;

/**
 * Classe contenente tutte le costanti utilizzate.
 */
public class Const {
    /**
     * Massima dimensione del buffer di lettura e scrittura.
     */
    public static final int DIM_MAX_BUF = 8 * 1024;

    /**
     * Valore della colonna in cui è contenuta la JProgressBar.
     */
    public static final int PROGRESS_BAR_COLUMN = 4;

    /**
     * Costante utilizzata in tutte le {@link BufferedSplitter#split() divisioni} per aggiungere l'estensione ".par" alla fine dei file.
     * @see BufferedSplitter#split()
     */
    public static final String SPLIT_EXTENSION = ".par";

    /**
     * Costante utilizzata nelle divisioni con {@link CryptoSplitter  cifratura} del contenuto per aggiungere l'estensione ".crypto".
     * @see CryptoSplitter#split()
     */
    public static final String CRYPT_EXTENSION = ".crypto";

    /**
     * Costante utilizzata nelle divisioni con {@link ZipSplitter  compressione} del contenuto per aggiungere l'estensione ".zip".
     * @see ZipSplitter#split()
     */
    public static final String ZIP_EXTENSION = ".zip";

    /**
     * Costante utilizzata in fase di riunione delle parti divise per distinguere il file creato aggiungendo una stringa al nome.
     * @see BufferedSplitter#merge()
     * @see CryptoSplitter#merge()
     * @see ZipSplitter#merge()
     */
    public static final String MERGE_EXTENSION = "_merge";

    /**
     * Titolo del JOptionPane che mostra l'errore nell'inserimento dei dati nei campi.
     */
    public static final String TITLE_FIELD_ERROR_MESSAGE = "Errore nell'input";

    /**
     * Stringa di errore mostrata nel caso non siano riempiti i campi necessari nel {@link gui.SettingsDialog} e nel {@link gui.PasswordMergeDialog}.
     */
    public static final String FIELD_ERROR_MESSAGE = "I campi richiesti non sono stati riempiti!";

    /**
     * Stringa per avvisare che la password inserita in fase di decrypt è errata.
     */
    public static final String PASSWORD_ERROR_MESSAGE = "Password errata!";

    /**
     * Stringa per avvisare che è finita l'unione del file selezionato per la ricomposizione.
     */
    public static final String FINISHED_MERGE_MESSAGE = "Finita l'unione del file.";

}
