package gui;

import splitters.Splitter;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class QueueTableModel extends DefaultTableModel {
    private Vector<Splitter> v = null;
    private String[] col = {"File", "Modalità", "Grandezza", "Directory", "Progresso"};

    public QueueTableModel(Object[] e, int rowCount, Vector<Splitter> vec){
        super(e, rowCount);
        v = vec;
    }

    @Override
    public int getRowCount() {
        if(v == null)
            return 0;
        return v.size();
    }

    @Override
    public int getColumnCount() {
        return col.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return col[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return col[columnIndex].getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Splitter re = (Splitter) v.elementAt(rowIndex);
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
            case 2: return (int)re.getStartFile().length();
            case 3: return re.getStartFile().getAbsolutePath();
            case 4: return re.getProgressBar();
            default: return null;
        }
    }

    /*@Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }*/
}
