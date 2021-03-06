package gui;

import splitters.BufferedSplitter;
import splitters.Splitter;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

import static utils.Const.PROGRESS_BAR_COLUMN;

/**
 * Classe che implementa il modello della tabella che rappresenta la coda di esecuzione.
 * Estende la classe {@link DefaultTableModel DefaultTableModel}.
 */
public class QueueTableModel extends DefaultTableModel {
    /**
     * Vettore che contiene tutti i valori da visualizzare nella tabella.
     */
    private Vector<Splitter> v = null;

    /**
     * Vettore di stringhe contenente il nome di tutte le colonne della tabella.
     */
    private String[] col = {"File", "Modalità", "Grandezza", "Directory", "Progresso"};

    /**
     * Costruttore del QueueTableModel.
     * @param e Vettore contenente i nomi delle colonne da mostrare.
     * @param rowCount Numero di righe iniziale del modello.
     * @param vec Vettore dei dati che viene aggangiato al modello.
     */
    public QueueTableModel(Object[] e, int rowCount, Vector<Splitter> vec){
        super(e, rowCount);
        v = vec;
    }

    /**
     * Usato per ottenere il numero di righe presenti attualmente.
     * Sovrascrive {@link DefaultTableModel#getRowCount() DefaultTableModel.getRowCount()}.
     * @return Il numero di righe attualmente nel modello.
     */
    @Override
    public int getRowCount() {
        if(v == null)
            return 0;
        return v.size();
    }

    /**
     * Metodo per ottenere il numero di colonne del modello.
     * Sovrascrive {@link DefaultTableModel#getColumnCount() DefaultTableModel.getColumnCount()}.
     * @return Numero di colonne nella tabella.
     */
    @Override
    public int getColumnCount() {
        return col.length;
    }

    /**
     * Metodo per ottenere il nome della colonna dato l'indice di essa.
     * Sovrascrive {@link DefaultTableModel#getColumnName(int) DefaultTableModel.getColumnName(int)}.
     * @param columnIndex Indice della colonna di cui si vuole sapere il nome.
     * @return Stringa contenente il nome della colonna.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return col[columnIndex];
    }

    /**
     * Metodo per ottenere la classe delle colonne dato l'indice di essa.
     * Sovrascrive {@link DefaultTableModel#getColumnClass(int) DefaultTableModel.getColumnClass(int)}.
     * @param columnIndex Indice della colonna di cui si vuole sapere la classe.
     * @return Classe della colonna che si è richiesta.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return col[columnIndex].getClass();
    }

    /**
     * Metodo per dire se una cella individuata da una riga e una colonna è editabile.
     * Sovrascrive {@link DefaultTableModel#isCellEditable(int, int)  DefaultTableModel.isCellEditable(int, int)}.
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
     * Sovrascrive {@link DefaultTableModel#getValueAt(int, int) DefaultTableModel.getValueAt(int, int)}.
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
                        if(((BufferedSplitter)re).isParti())
                            return "Splitter";
                        else
                            return "Parts";
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
     * @see javax.swing.JProgressBar
     */
    public void updateStatus(int index) {
        if(v.elementAt(index) != null){
            fireTableCellUpdated(index, PROGRESS_BAR_COLUMN);
        }
    }
}
