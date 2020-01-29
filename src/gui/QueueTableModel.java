package gui;

import splitters.Splitter;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

import static utils.Const.PROGRESS_BAR_COLUMN;

/**
 * Classe che estende DefaultTableModel, implementa il modello attuale della tabella con 5 colonne.
 */
public class QueueTableModel extends DefaultTableModel {
    /**
     * Vector che contiene tutti i valori da visualizzare nella tabella.
     */
    private Vector<Splitter> v = null;

    /**
     * Vettore di stringhe contenente il nome di tutte le colonne della tabella.
     */
    private String[] col = {"File", "Modalità", "Grandezza", "Directory", "Progresso"};

    /**
     * Costruttore del TableModel.
     * @param e Vettore contenente i nomi delle colonne da mostrare.
     * @param rowCount Numero di righe iniziale del modello.
     * @param vec Vector dei dati che viene aggangiato al modello.
     */
    public QueueTableModel(Object[] e, int rowCount, Vector<Splitter> vec){
        super(e, rowCount);
        v = vec;
    }

    /**
     * Metodo sovrascritto da DefaultTableModel.
     * Metodo usato per ottenere il numero di righe presenti attualmente.
     * @return Il numero di righe attualmente nel modello.
     */
    @Override
    public int getRowCount() {
        if(v == null)
            return 0;
        return v.size();
    }

    /**
     * Metodo per ottenere il numero di colonne nel modello.
     * @return Numero di colonne nella tabella.
     */
    @Override
    public int getColumnCount() {
        return col.length;
    }

    /**
     * Metodo per ottenere il nome della colonna dato l'indice di essa.
     * @param columnIndex Indice della colonna di cui si vuole sapere il nome.
     * @return Stringa contenente il nome della colonna.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return col[columnIndex];
    }

    /**
     * Metodo per ottenere la classe delle colonna dato l'indice di essa.
     * @param columnIndex Indice della colonna di cui si vuole sapere la classe.
     * @return Classe della colonna che si è richiesta.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return col[columnIndex].getClass();
    }

    /**
     * Metodo per dire se una cella è editabile o no a seconda della colonna.
     * @param rowIndex Indice della riga da controllare.
     * @param columnIndex Indice della colonna da controllare.
     * @return Restituisce true nel caso sia editabile, false altrimenti.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Metodo per disegnare la tabella e i valori in ogni sua colonna.
     * @param rowIndex Indice della riga da cui prendere i dati.
     * @param columnIndex Indice della colonna da riempire.
     * @return Valore che andrà inserito della cella specificata.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Splitter re = v.elementAt(rowIndex);
        switch (columnIndex){
            case 0: return re.getStartFile().getName();
            case 1:
                switch(re.getClass().getCanonicalName()){
                    case "splitters.BufferedSplitter":
                        return "Splitter";
                    case "splitters.CryptoSplitter":
                        return "Crypto";
                    case "splitters.ZipSplitter":
                        return "Zip";
                }
                return re.getClass().getCanonicalName();
            case 2: return (int) re.getStartFile().length();
            case 3: return re.getFinalDirectory();
            case 4: return (re.getProgress()/re.getStartFile().length()*100);
            default: return null;
        }
    }

    /**
     * Metodo usato per aggiornare il valore della colonna contenente la JProgressBar.
     * @param index Indice del file di cui c'è da aggiornare lo stato.
     * @param newValue Nuovo valore da inserire nella cella.
     */
    public void updateStatus(int index, int newValue) {
        if(v.elementAt(index) != null){
            setValueAt(newValue, index, PROGRESS_BAR_COLUMN);
            fireTableCellUpdated(index, PROGRESS_BAR_COLUMN);
        }
    }

    /**
     * Metodo per aggiornare i valori della tabella.
     * @param value Nuovo valore da impostare.
     * @param rowIndex Riga da aggiornare.
     * @param columnIndex Colonna da aggiornare.
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case PROGRESS_BAR_COLUMN:
                if (value instanceof Double) {
                    v.elementAt(rowIndex).setProgress((double) value);
                }
                break;
        }
    }
}
