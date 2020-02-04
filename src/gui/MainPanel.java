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
import java.io.File;
import java.util.List;
import java.util.Vector;

import static utils.Const.*;

/**
 * Classe che implementa il pannello principale della gui.
 * Qui sono contenuti la tabella, il suo modello e i bottoni per le funzionalità.
 * @see JPanel
 */
public class MainPanel extends JPanel {
    /**
     * JButton che rappresenta il bottone per avviare la divisione dei file in coda.
     */
    private JButton startQueueButton;

    /**
     * JButton che rappresenta il bottone per aggiungere un nuovo file alla coda di divisione.
     */
    private JButton addSplitButton;

    /**
     * JButton che rappresenta il bottone per selezionare un file da unire con le sue altre parti.
     */
    private JButton addMergeButton;

    /**
     * JButton che rappresenta il bottone per modificare i file selezionati in coda.
     */
    private JButton editButton;

    /**
     * JButton che rappresenta il bottone per cancellare i file selezionati dalla coda.
     */
    private JButton deleteButton;

    /**
     * Modello su cui si basa la tabella.
     * @see QueueTableModel
     */
    private QueueTableModel data;

    /**
     * Vettore che contiene tutte le righe della tabella, ognuna rappresenta un file su cui lavorare.
     * @see Splitter
     */
    private Vector<Splitter> v = new Vector<>();

    /**
     * Vettore usato dagli SwingWorker per capire qual è l'ultimo ad eseguire e riabilitare i bottoni.
     */
    private Vector<Boolean> completed;

    /**
     * Implementazione della tabella che rappresenta la coda di divisione dei file.
     */
    private JTable tab;

    /**
     * Metodo di appoggio per aggiungere un nuovo file al vettore delle righe una volta che sono state scelte le relative impostazioni.
     * @param att File che si sta aggiungendo alla coda.
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
     * @see ActionListener
     */
    private class AggiuntaActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone si apre un JFileChooser per scegliere più file da aggiungere e per ognuno si aprono le impostazioni in un {@link SettingsDialog SettingDialog}.
         * Infine i nuovi file sono aggiunti al vettore con {@link #addElementToVector(File, SettingsDialog) addElementToVector} e la tabella viene aggiornata.
         * @param e Evento che è stato generato.
         * @see JFileChooser
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //il filechooser mi fa scegliere più file da aggiungere
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            if(chooser.showDialog(getParent(), "Apri") == JFileChooser.APPROVE_OPTION) {
                File[] scelte = chooser.getSelectedFiles();

                //per ogni file apro un dialog
                for (File att : scelte) {
                    SettingsDialog dialog = new SettingsDialog(att);
                    dialog.pack();
                    dialog.setLocation(0, 0);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                        //se tutti i campi sono completi allora lo aggiungo alla tabella
                    if (dialog.getState())
                        addElementToVector(att, dialog);
                }
                //aggiorno la tabella
                data.fireTableDataChanged();
            }
        }
    }

    /**
     * Classe per effettuare la divisione dei file su thread paralleli e nel frattempo aggiornare la grafica della tabella.
     * Fa uso di uno SwingWorker per gestire i thread.
     * @see SwingWorker
     */
    private class StartWorker extends SwingWorker<Boolean, Integer>{
        /**
         * Indice della riga rappresentante il file da dividere nell'array di tutti i file.
         */
        private int index;

        /**
         * Costruttore dello StartWorker.
         * @param i Indice della riga rappresentante il file da dividere.
         */
        public StartWorker(int i){
            index = i;
        }

        /**
         * Metodo che esegue i calcoli della divisione su un thread parallelo e nel frattempo
         * invia i risultati parziali al metodo {@link #process(List) process()} tramite publish().
         * Usato per far partire le divisioni dei file nella coda e seguire il loro andamento.
         * @return Ritorna il valore sullo stato dell'esecuzione del thread parallelo, se è finito con successo o meno.
         */
        @Override
        protected Boolean doInBackground() {
            setProgress(0);                         //il progresso parte da zero

            Thread t = new Thread(v.elementAt(index));      //viene fatto partire il thread
            t.start();

            while(t.getState() != Thread.State.TERMINATED){
                //finché non è finita la divisione continuo a modificare il valore del progresso
                double progress = (v.elementAt(index).getProgress() / v.elementAt(index).getStartFile().length() * 100f);
                setProgress((int)progress);
                publish((int)progress);
            }

            //setto il progresso alla fine
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
            data.updateStatus(index);
        }

        /**
         * Metodo invocato alla fine di {@link #doInBackground() doInBackground()}.
         * Si occupa di capire se lo StartWorker appena terminato era l'ultimo e nel caso riabilita i bottoni della gui.
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
     * Listener che viene agganciato al bottone per avviare l'esecuzione della divisione dei file in coda.
     */
    private class StartActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone per la partenza viene fatta
         * partire la divisione su un thread diverso per ogni file.
         * I bottoni vengono disabilitati fino alla fine di tutte le divisioni.
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
     * Listener che viene agganciato al bottone per eliminare file selezionati dalla coda.
     */
    private class EliminaActionListener implements ActionListener{

        /**
         * Una volta premuto il bottone per l'eliminazione vengono tolti dalla tabella i file scelti.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] a = tab.getSelectedRows();            //ottengo tutte le righe selezionate
            if(a.length != 0){
                for (int i = a.length-1; i>=0; i--){    //scorro all'indietro per evitare "shift" di valori
                    v.remove(a[i]);                     //li rimuovo dal vettore
                }
                data.fireTableDataChanged();            //aggiorno la tabella
            }
        }
    }

    /**
     * Listener che viene agganciato al bottone per modificare i parametri dei file selezionati dalla coda.
     */
    private class ModificaActionListener implements ActionListener{

        /**
         * Viene riaperto il {@link SettingsDialog SettingsDialog} per i file selezionati da modificare.
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
                    //se tutti i campi sono stati completati
                    File attuale = tmp.getStartFile();
                    if(sd.getState()){
                        v.remove(a[i]);
                        addElementToVector(attuale, sd);
                    }
                }

                //aggiorno la tabella
                data.fireTableDataChanged();
            }
        }

    }

    /**
     * Listener che viene agganciato al bottone per selezionare un file da unire al resto delle sue parti.
     */
    private class UnioneActionListener implements ActionListener{

        /**
         * Viene aperto un JFileChooser per permettere di scegliere il file da unire.
         * Una volta scelto viene iniziato il processo di unione a seconda del tipo di divisione che era stata effettuata.
         * Nel caso sia richiesta una password compare un {@link PasswordMergeDialog Dialog} per inserirla.
         * @param e Evento che è stato generato.
         * @see JFileChooser
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            //l'unione inizia solo se un file è selezionato
            if(chooser.showDialog(getParent(), "Apri") == JFileChooser.APPROVE_OPTION) {
                new UnioneWorker(chooser.getSelectedFile()).execute();
            }
        }
    }

    /**
     * Classe per effettuare l'unione delle parti di un file su un thread parallelo.
     * Fa uso di uno SwingWorker per gestire i thread.
     * @see SwingWorker
     */
    private class UnioneWorker extends SwingWorker<Boolean, Void>{

        /**
         * File iniziale da cui far partire l'unione.
         */
        private File fileDaUnire;

        /**
         * Costruttore del Worker.
         * @param daUnire File da unire alle altre sue parti.
         */
        public UnioneWorker(File daUnire){
            fileDaUnire = daUnire;
        }

        /**
         * Metodo usato per eseguire in background l'unione dei file.
         * Prima il metodo capisce in che modo è stato diviso il file e infine fa partire l'esecuzione.
         * @return Ritorna il valore sullo stato dell'esecuzione del thread parallelo, se è finito con successo o meno.
         */
        @Override
        protected Boolean doInBackground(){
            Thread t = null;
            //deve scegliere che tipo di unione avviare a seconda dell'estensione
            switch (fileDaUnire.getName().substring(fileDaUnire.getName().lastIndexOf("."), fileDaUnire.getName().length())) {
                case ".crypto":
                    PasswordMergeDialog dialog = new PasswordMergeDialog();
                    dialog.pack();
                    dialog.setLocation(0, 0);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    //controllo che il campo password sia stato inserito
                    if (dialog.getState())
                        t = new Thread(new CryptoSplitter(fileDaUnire, false, new String(dialog.getPassValue().getPassword())));
                    break;
                case ".zip":
                    t = new Thread(new ZipSplitter(fileDaUnire, false));
                    break;
                case ".par":
                    t = new Thread(new BufferedSplitter(fileDaUnire, false));
                    break;
            }
            if (t != null)
                t.start();          //avvio il thread creato

            return true;
        }
    }

    /**
     * Classe che implementa il Render per una cella specifica della tabella che contiene la JProgressBar.
     * @see TableCellRenderer
     * @see JProgressBar
     */
    private class ProgressCellRender extends JProgressBar implements TableCellRenderer {
        /**
         * Metodo che crea e aggiorna la JProgressBar con un certo valore indicato.
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
            setStringPainted(true);             //percentuale nella progress bar
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

        //setto il render per la cella contenente la progressbar
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
    }
}