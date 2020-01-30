package gui;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.Splitter;
import splitters.ZipSplitter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Vector;

import static utils.Const.*;

/**
 * Classe che implementa il pannello principale della grafica.
 * Qui sono contenuti la tabella, il suo modello e i bottoni per le funzionalità.
 */
public class MainPanel extends JPanel {
    /**
     * JButton che rappresenta il bottone per avviare la divisione dei files in coda di divisone.
     */
    private JButton startQueueButton;

    /**
     * JButton che rappresenta il bottone per aggiungere un nuovo file alla coda di divisione.
     */
    private JButton addSplitButton;

    /**
     * JButton che rappresenta il bottone per selezionare un file da unire.
     */
    private JButton addMergeButton;

    /**
     * JButton che rappresenta il bottone per modificare i files che sono nella coda di divisione.
     */
    private JButton editButton;

    /**
     * JButton che rappresenta il bottone per cancellare i files dalla coda di divisione.
     */
    private JButton deleteButton;

    /**
     * Modello su cui si basa la tabella.
     */
    private QueueTableModel data;

    /**
     * Vettore che contiene tutte le righe della tabella.
     */
    private Vector<Splitter> v = new Vector<>();

    /**
     * Vettore usato dagli SwingWorker per capire qual è l'ultimo e riabilitare i bottoni.
     */
    private Vector<Boolean> completed;

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

                if(dialog.getState())
                    addElementToVector(att, dialog);
            }

            data.fireTableDataChanged();
        }
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
         */
        @Override
        protected Boolean doInBackground() {
            setProgress(0);

            Thread t = new Thread(v.elementAt(index));
            t.start();

            while(t.getState() != Thread.State.TERMINATED){
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

        /**
         * Metodo invocato alla fine di doInBackground(). Si occupa di capire se lo StartWorker
         * appena terminato era l'ultimo e nel caso riabilita i bottoni della gui.
         */
        @Override
        protected void done(){
            //dico che un altro SwingWorker è stato completato
            completed.add(true);


            //se il numero di completati è pari alla lunghezza della coda allora riabilito i bottoni
            if(completed.size() == v.size()) {
                startQueueButton.setEnabled(true);
                addSplitButton.setEnabled(true);
                addMergeButton.setEnabled(true);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        }
    }

    /**
     * Listener che viene agganciato al bottone per avviare il processo di split/merge.
     */
    private class StartActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone per la partenza viene svuotata tutta la coda facendo partire la divisione su un thread diverso per ogni file.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //vettore che conta i completati nella coda per poter riabilitare i bottoni
            completed = new Vector<>(0);
            for(Splitter sp : v){               //in questo modo tengo conto dei file che sono finiti ma ancora non
                if(sp.getProgress() != 0)       //eliminati in coda dall'utente
                    completed.add(true);
            }

            //disabilito i bottoni durante la divisione
            //se ci sono file in coda che non sono ancora stati divisi
            if(v.size() != completed.size()) {
                startQueueButton.setEnabled(false);
                addSplitButton.setEnabled(false);
                addMergeButton.setEnabled(false);
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }


            //per ogni file in coda comincio la sua divisione su un thread diverso
            for(Splitter sp : v) {
                if (sp.getProgress() == 0)
                    new StartWorker(v.indexOf(sp)).execute();
            }
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

                    //creo il JDialog
                    sd.pack();
                    sd.setLocation(0, 0);
                    sd.setLocationRelativeTo(null);
                    sd.setVisible(true);

                    //rimuovo il file vecchio e lo rimetto aggiornato nella tabella
                    File attuale = tmp.getStartFile();
                    if(sd.getState()){
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

                    //controllo che il campo password sia stato inserito
                    if(dialog.getPassValue().getPassword().length != 0)
                        t = new Thread(new CryptoSplitter(att, false, new String(dialog.getPassValue().getPassword())));
                    else
                        JOptionPane.showMessageDialog(getParent(), FIELD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    break;
                case ".zip":
                    t = new Thread(new ZipSplitter(att, false));
                    break;
                case ".par":
                    t = new Thread(new BufferedSplitter(att, false));
                    break;
            }
            if(t != null)
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
        startQueueButton = new JButton("Avvio");
        startQueueButton.addActionListener(new StartActionListener());
        startQueueButton.setActionCommand("avvio");
        add(startQueueButton);

        //bottone per aggiungere un file da dividere
        addSplitButton = new JButton("Dividi");
        addSplitButton.addActionListener(new AggiuntaActionListener());
        addSplitButton.setActionCommand("divisione");
        add(addSplitButton);

        //bottone per aggiungere un file da unire
        addMergeButton = new JButton("Unisci");
        addMergeButton.addActionListener(new UnioneActionListener());
        addMergeButton.setActionCommand("unione");
        add(addMergeButton);

        //bottone per modificare un file nella tabella
        editButton = new JButton("Modifica");
        editButton.addActionListener(new ModificaActionListener());
        editButton.setActionCommand("modifica");
        add(editButton);

        //bottone per eliminare un file dalla tabella
        deleteButton = new JButton("Elimina");
        deleteButton.addActionListener(new EliminaActionListener());
        deleteButton.setActionCommand("elimina");
        add(deleteButton);

        this.registerKeyboardAction(new EliminaActionListener(), KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
}