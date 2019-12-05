package view;

import splitters.Splitter;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class QueueTableModel extends DefaultTableModel {
    private Vector<RowElement> v = null;
    private String[] col = {"File", "Modalità", "Grandezza", "Directory", "Progresso"};

    public QueueTableModel(Object[] e, int rowCount, Vector<RowElement> vec){
        super(e, rowCount);
        v = vec;
    }

    @Override
    public int getRowCount() {
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
        if (columnIndex == 1)
            return true;
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RowElement re = (RowElement) v.elementAt(rowIndex);
        switch (columnIndex){
            case 0: return re.getTask().getStartFile().getName();
            case 1: return re.getMod();
            case 2: return (int)re.getTask().getStartFile().length();
            case 3: return re.getTask().getStartFile().getAbsolutePath();
            case 4: return re.getProgress();
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
