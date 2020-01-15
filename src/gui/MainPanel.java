package gui;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.Splitter;
import splitters.ZipSplitter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
     * Implementazione della tabella.
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
                v.add(new BufferedSplitter(att, Integer.parseInt(dialog.getDimValue().getText())));
                break;
            case 1:
                v.add(new CryptoSplitter(att, new String(dialog.getPassValue().getPassword()), Integer.parseInt(dialog.getDimValue().getText())));
                break;
            case 2:
                v.add(new ZipSplitter(att, Integer.parseInt(dialog.getDimValue().getText())));
                break;
            case 3:
                v.add(new BufferedSplitter(att, Long.parseLong(dialog.getnPartiValue().getText())));
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

                addElementToVector(att, dialog);
            }

            data.fireTableDataChanged();
        }
    }

    /**
     * Listener che viene agganciato al bottone per avviare il processo di split/merge.
     */
    private class StartActionListener implements ActionListener {

        /**
         * Una volta premuto il bottone per la partenza viene svuotata tutta la coda facendo partire la divisione su un thread diverso per ogni file.
         * Alla fine il vettore è svuotato e la tabella aggiornata.
         * @param e Evento che è stato generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            for(Splitter sp : v){
                sp.run();
            }
            v.removeAllElements();
            data.fireTableDataChanged();
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
                    sd.setBounds(0, 0, 500, 500);
                    sd.setLocationRelativeTo(null);
                    sd.setVisible(true);

                    //rimuovo il file vecchio e lo rimetto aggiornato nella tabella
                    File attuale = tmp.getStartFile();
                    v.remove(a[i]);
                    addElementToVector(attuale, sd);
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

                    t = new Thread(new CryptoSplitter(att, new String(dialog.getPassValue().getPassword())));
                    break;
                case ".zip":
                    t = new Thread(new ZipSplitter(att));
                    break;
                case ".par":
                    t = new Thread(new BufferedSplitter(att));
                    break;
            }
            t.start();
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