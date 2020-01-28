package gui;

import static utils.Const.PROGRESS_BAR_COLUMN;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.Splitter;
import splitters.ZipSplitter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Classe che implementa il pannello principale della grafica.
 * Qui sono contenuti la tabella, il suo modello e i bottoni per le funzionalità.
 */
public class MainPanel extends JPanel {
    /**
     * Modello su cui si basa la tabella.
     */
    private QueueTableModel data;

    /**
     * Vettore che contiene tutte le righe della tabella.
     */
    private Vector<Splitter> v = new Vector<>();

    /**
     * Implementazione della tabella che rappresenta la coda di divisione dei file.
     */
    private JTable tab;

    /**
     * Metodo di appoggio per aggiungere un nuovo file al vettore delle righe una volta che sono state scelte le relative impostazioni.
     * @param att File che si sta caricando.
     * @param dialog Dialog personalizzato contenente i dati da mettere nella tabella e per creare gli oggetti Splitter.
     */
    private void addElementToVector(File att, SettingsDialog dialog) {
        switch(dialog.getModValue().getSelectedIndex()){
            case 0:
                v.add(new BufferedSplitter(att, true, Integer.parseInt(dialog.getDimValue().getText()), dialog.getDirLabel().getText()));
                break;
            case 1:
                v.add(new CryptoSplitter(att, true, new String(dialog.getPassValue().getPassword()), Integer.parseInt(dialog.getDimValue().getText()), dialog.getDirLabel().getText()));
                break;
            case 2:
                v.add(new ZipSplitter(att, true, Integer.parseInt(dialog.getDimValue().getText()), dialog.getDirLabel().getText()));
                break;
            case 3:
                v.add(new BufferedSplitter(att, true, Long.parseLong(dialog.getnPartiValue().getText()), dialog.getDirLabel().getText()));
                break;
        }
    }

    /**
     * Listener che viene agganciato al bottone di aggiunta di un nuovo file.
     */
    private class AggiuntaActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone si apre un JFileChooser per scegliere più file da aggiungere e per ognuno si aprono le impostazioni in un Dialog.
         * Infine i nuovi file sono aggiunti al vettore con addElementToVector(File att, SettingsDialog dialog) e la tabella viene aggiornata.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.showDialog(getParent(), "Apri");
            File[] scelte = chooser.getSelectedFiles();

            for(File att : scelte){
                SettingsDialog dialog = new SettingsDialog(att);
                dialog.pack();
                dialog.setLocation(0, 0);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                if(validateFields(dialog))
                    addElementToVector(att, dialog);
            }

            data.fireTableDataChanged();
        }
    }

    /**
     * Metodo privato per controllare che i campi richiesti dal JDialog siano tutti riempiti alla sua chiusura.
     * @param dialog Il JDialog da controllare.
     * @return true se i campi sono tutti completati, false altrimenti.
     */
    private boolean validateFields(SettingsDialog dialog) {
        switch (dialog.getModValue().getSelectedIndex()){
            case 0: //se è la divisione "classica"
            case 2: //se è la divisione con compressione
                if(dialog.getDimValue().getText().length() == 0)
                    return false;
                break;
            case 1: //se è la divisione con cifratura
                if(dialog.getDimValue().getText().length() == 0 || dialog.getPassValue().getPassword().length == 0)
                    return false;
                break;
            case 3: //se è la divisione con numero di parti
                if(dialog.getnPartiValue().getText().length() == 0)
                    return false;
                break;
        }

        return true;
    }

    /*
    * https://stackoverflow.com/questions/20260372/swingworker-progressbar
    * https://docs.oracle.com/javase/7/docs/api/javax/swing/SwingWorker.html#publish(V...)
    * http://www.herongyang.com/Swing/SwingWorker-Example-using-JProgressBar.html
    * https://docs.oracle.com/javase/7/docs/api/javax/swing/table/TableCellRenderer.html
    *
    * https://stackoverflow.com/questions/13753562/adding-progress-bar-to-each-table-cell-for-file-progress-java
    */

    /**
     * Classe per effettuare la divisione dei file su thread paralleli e nel frattempo aggiornare la grafica della tabella.
     */
    private class StartWorker extends SwingWorker<Boolean, Integer>{
        /**
         * Indice della riga rappresentante il file da dividere nell'array di tutti i files.
         */
        private int index;

        /**
         * Costruttore dello SwingWorker.
         * @param i Indice della riga rappresentante il file da dividere.
         */
        public StartWorker(int i){
            index = i;
        }

        /**
         * Metodo che esegue i calcoli del componente su un thread parallelo e nel frattempo invia i risultati parziali al metodo process() tramite publish().
         * Usato per far partire le divisioni dei file nella coda e seguire il loro andamento.
         * @return Ritorna il valore sullo stato dell'esecuzione del thread parallelo, se è finito o meno.
         * @throws Exception
         */
        @Override
        protected Boolean doInBackground() throws Exception {
            setProgress(0);

            Thread t = new Thread(v.elementAt(index));
            t.start();

            while(t.getState() != Thread.State.TERMINATED){
                //Thread.sleep(100);
                double progress = (v.elementAt(index).getProgress() / v.elementAt(index).getStartFile().length() * 100f);
                setProgress((int)progress);
                publish((int)progress);
            }

            setProgress(100);
            publish(100);
            return true;
        }

        /**
         * Metodo invocato da publish() per aggiornare su un thread parallelo a quello di esecuzione la grafica.
         * @param chunks Nuovo valore da inserire.
         */
        @Override
        protected void process(List<Integer> chunks){
            data.updateStatus(index, chunks.get(0));
        }
    }

    /**
     *
     * Listener che viene agganciato al bottone per avviare il processo di split/merge.
     */
    private class StartActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone per la partenza viene svuotata tutta la coda facendo partire la divisione su un thread diverso per ogni file.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            for(Splitter sp : v){
                if(sp.getProgress() == 0)
                    new StartWorker(v.indexOf(sp)).execute();
            }
            //v.removeAllElements();
            //data.fireTableDataChanged();
        }
    }

    /**
     * Listener che viene agganciato al bottone per eliminare file dalla coda.
     */
    private class EliminaActionListener implements ActionListener{

        /**
         * Una volta premuto il bottone per l'eliminazione vengono tolti dalla tabella i file scelti da eliminare.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] a = tab.getSelectedRows();
            if(a.length != 0){
                for (int i = a.length-1; i>=0; i--){
                    v.remove(a[i]);
                }
                data.fireTableDataChanged();
            }
        }
    }

    /**
     * Listener che viene agganciato al bottone per modificare i parametri dei file in coda.
     */
    private class ModificaActionListener implements ActionListener{

        /**
         * Viene riaperto il dialog delle impostazioni per i file selezionato da modificare.
         * Il file modificato verrà poi eliminato dalla tabella e reinserito aggiornato.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] a = tab.getSelectedRows();
            if(a.length!=0) {
                for (int i = a.length - 1; i >= 0; i--) {
                    Splitter tmp = v.get(a[i]);
                    SettingsDialog sd = null;

                    //controllo di che tipo è il file selezionato attualmente
                    if (tmp instanceof BufferedSplitter) {
                        sd = new SettingsDialog((BufferedSplitter) tmp);
                    } else if (tmp instanceof CryptoSplitter) {
                        sd = new SettingsDialog((CryptoSplitter) tmp);
                    } else if (tmp instanceof ZipSplitter) {
                        sd = new SettingsDialog((ZipSplitter) tmp);
                    }

                    //creo il Dialog
                    sd.pack();
                    sd.setLocation(0, 0);
                    sd.setLocationRelativeTo(null);
                    sd.setVisible(true);

                    //rimuovo il file vecchio e lo rimetto aggiornato nella tabella
                    File attuale = tmp.getStartFile();
                    if(validateFields(sd)){
                        v.remove(a[i]);
                        addElementToVector(attuale, sd);
                    }
                }

                data.fireTableDataChanged();
            }
        }

    }

    /**
     * Listener che viene agganciato al bottone per selezionare un file da unire.
     */
    private class UnioneActionListener implements ActionListener{

        /**
         * Viene aperto un JFileChooser per permettere di scegliere il file da riunire.
         * Una volta scelto viene iniziato il processo di unione a seconda del tipo di divisione che era stata effettuata.
         * Nel caso sia richiesta una password compare un Dialog per inserirla.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.showDialog(getParent(), "Apri");
            File att = chooser.getSelectedFile();

            Thread t = null;
            switch (att.getName().substring(att.getName().lastIndexOf("."), att.getName().length())){
                case ".crypto":
                    PasswordMergeDialog dialog = new PasswordMergeDialog();
                    dialog.pack();
                    dialog.setLocation(0, 0);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    t = new Thread(new CryptoSplitter(att, false, new String(dialog.getPassValue().getPassword())));
                    break;
                case ".zip":
                    t = new Thread(new ZipSplitter(att, false));
                    break;
                case ".par":
                    t = new Thread(new BufferedSplitter(att, false));
                    break;
            }
            t.start();
        }
    }

    /**
     * Classe che implementa il Render per una cella specifica della tabella che contiene la JProgressBar.
     */
    private class ProgressCellRender extends JProgressBar implements TableCellRenderer {
        /**
         * Metodo che crea la JProgressBar con un certo valore iniziale oppure con un valore passato.
         * @param table La tabella su cui si sta lavorando.
         * @param value Il valore del campo da aggiornare.
         * @param isSelected Valore per capire se la cella è selezionata.
         * @param hasFocus Valore per capire se la cella ha il focus.
         * @param row Indice della riga.
         * @param column Indice della colonna.
         * @return Il componente da inserire nella cella specificata.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setStringPainted(true);
            int progress = 0;
            if (value instanceof Double) {
                progress = (int) Math.round(((Double) value));
            }else
            if (value instanceof Integer) {
                progress = (int) value;
            }
            setValue(progress);
            return this;
        }
    }

    /**
     * Costruttore del pannello che inizializza la tabella e aggiunge i bottoni.
     */
    public MainPanel(){
        //qui viene creata tutta la tabella con il relativo TableModel
        String[] col = {"File", "Modalità", "Grandezza", "Directory", "Progresso"};
        data = new QueueTableModel(col, 0, v);
        tab = new JTable(data);
        tab.setSize(800, 500);

        tab.getColumn(data.getColumnName(PROGRESS_BAR_COLUMN)).setCellRenderer(new ProgressCellRender());

        add(tab);
        add(new JScrollPane(tab));

        //bottone per l'avvio dei processi
        JButton startQueueButton = new JButton("Avvio");
        startQueueButton.addActionListener(new StartActionListener());
        startQueueButton.setActionCommand("avvio");
        add(startQueueButton);

        //bottone per aggiungere un file da dividere
        JButton addSplitButton = new JButton("Dividi");
        addSplitButton.addActionListener(new AggiuntaActionListener());
        addSplitButton.setActionCommand("divisione");
        add(addSplitButton);

        //bottone per aggiungere un file da unire
        JButton addMergeButton = new JButton("Unisci");
        addMergeButton.addActionListener(new UnioneActionListener());
        addMergeButton.setActionCommand("unione");
        add(addMergeButton);

        //bottone per modificare un file nella tabella
        JButton editButton = new JButton("Modifica");
        editButton.addActionListener(new ModificaActionListener());
        editButton.setActionCommand("modifica");
        add(editButton);

        //bottone per eliminare un file dalla tabella
        JButton deleteButton = new JButton("Elimina");
        deleteButton.addActionListener(new EliminaActionListener());
        deleteButton.setActionCommand("elimina");
        add(deleteButton);
    }
}