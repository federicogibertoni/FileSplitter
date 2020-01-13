package gui;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.ZipSplitter;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

/**
 * Classe che implementa il Dialog che viene aperto quando si aggiunge un nuovo file alla coda oppure se si vogliono modificare le impostazioni.
 */
public class SettingsDialog extends JDialog {
    /**
     * Pannello che contiene il Dialog.
     */
    private JPanel contentPane;
    /**
     * Bottone per dare la conferma dei dati inseriti.
     */
    private JButton buttonOK;
    /**
     * Bottone per annullare l'inserimento dei dati inseriti.
     */
    private JButton buttonCancel;
    /**
     * ComboBox che permette di effettuare la scelta del tipo di divisione da effettuare.
     */
    private JComboBox modValue;
    /**
     * Campo di testo che permette di specificare la dimensione massima di ogni file della divisione.
     */
    private JTextField dimValue;
    /**
     * Campo di testo per inserire la password nella codifica dei file.
     */
    private JPasswordField passValue;
    /**
     * Campo di testo per inserire il numero di parti uguali in cui dividere il file nella coda.
     */
    private JTextField nPartiValue;
    /**
     * Label che andrà ad indicare il campo di testo per inserire la dimensione.
     */
    private JLabel dimLabel;
    /**
     * Label che andrà ad indicare il campo di testo per inserire la password.
     */
    private JLabel passLabel;
    /**
     * Label che andrà ad indicare il campo di testo per inserire il numero di parti nella divisione.
     */
    private JLabel nPartiLabel;
    /**
     * Label che andrà ad indicare la ComboBox per la scelta della modalità di divisione.
     */
    private JLabel modLabel;
    /**
     * Label che andrà ad indicare il file attuale che si sta trattando.
     */
    private JLabel fileName;

    /**
     * Classe interna che implementa il listener per animare i campi del dialog a seconda della selezione della JComboBox.
     */
    private class ComboSelectionListener implements ActionListener{

        /**
         * Implementazione del listener.
         * A seconda del valore della JComboBox abilita o disabilita i componenti relativi alla modalità scelta.
         * @param e Evento generato.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (modValue.getSelectedIndex()){
                case 0:         //BufferedSplitter con dimensione specificata
                case 2:         //ZipSplitter
                    dimLabel.setEnabled(true);
                    dimValue.setEnabled(true);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 1:         //CryptoSplitter
                    dimLabel.setEnabled(true);
                    dimValue.setEnabled(true);
                    passLabel.setEnabled(true);
                    passValue.setEnabled(true);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 3:         //BufferedSplitter con numero parti
                    dimLabel.setEnabled(false);
                    dimValue.setEnabled(false);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
                    nPartiLabel.setEnabled(true);
                    nPartiValue.setEnabled(true);
                    break;
            }
        }
    }

    /**
     * Metodo privato per creare delle istanze di ogni componente del Dialog.
     */
    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setOpaque(true);
        this.setContentPane(contentPane);

        setModal(true);         //blocca l'input nelle altre finestre

        buttonCancel = new JButton("Annulla");
        buttonOK = new JButton("Ok");
        getRootPane().setDefaultButton(buttonOK);  //nel pannello di base metto come bottone di default di chiusura quello di OK

        modValue = new JComboBox();
        modValue.addItem("Splitter");
        modValue.addItem("Crypter");
        modValue.addItem("Zipper");
        modValue.addItem("Parts");

        dimValue = new JTextField();
        dimValue.setColumns(30);

        passValue = new JPasswordField();
        passValue.setColumns(30);

        nPartiValue = new JTextField();
        nPartiValue.setColumns(30);

        modLabel = new JLabel("Modalità");
        dimLabel = new JLabel("Dimensione");
        passLabel = new JLabel("Password");
        nPartiLabel = new JLabel("N. Parti");
        fileName = new JLabel();
    }

    /**
     * Metodo privato per aggiungere tutte le istanze create al pannello del Dialog.
     */
    private void addComponents() {
        add(fileName);

        add(modLabel);
        add(modValue);

        add(dimLabel);
        add(dimValue);

        add(passLabel);
        add(passValue);

        add(nPartiLabel);
        add(nPartiValue);

        add(buttonOK);
        add(buttonCancel);
    }

    /**
     * Costruttore chiamato durante l'inserimento di un nuovo valore nella tabella.
     * @param att File che è elaborato attualmente.
     */
    public SettingsDialog(File att) {
        super();

        initComponents();
        addComponents();

        //aggiungo listener ai bottoni
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        //quando si chiude la finestra viene chiamato onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        //quando la finestra è chiusa da ESC si chiama onCancel()
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //aggiungo il listener al selettore di modalità
        modValue.addActionListener(new ComboSelectionListener());

        //imposto attiva solo la prima casella, cioè quella di default rispetto al combobox
        //modValue.setSelectedIndex(0);
        dimLabel.setEnabled(true);
        dimValue.setEnabled(true);
        passLabel.setEnabled(false);
        passValue.setEnabled(false);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);

        fileName.setText(att.getPath());
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file settato con BufferedSplitter.
     * @param tmp BufferedSplitter che sarà modificato, da cui prendo i dati da mostrare.
     */
    public SettingsDialog(BufferedSplitter tmp) {
        this(tmp.getStartFile());

        passLabel.setEnabled(false);
        passValue.setEnabled(false);

        if(tmp.isParti()){
            dimLabel.setEnabled(false);
            dimValue.setEnabled(false);
            nPartiLabel.setEnabled(true);
            nPartiValue.setEnabled(true);

            nPartiValue.setText(Long.toString(tmp.getnParti()));

            modValue.setSelectedIndex(3);
        }
        else{
            dimLabel.setEnabled(true);
            dimValue.setEnabled(true);
            nPartiLabel.setEnabled(false);
            nPartiValue.setEnabled(false);

            dimValue.setText(Integer.toString(tmp.getDimPar()));

            modValue.setSelectedIndex(0);
        }
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file settato con CryptoSplitter.
     * @param c CryptoSplitter che sarà modificato, da cui prendo i dati da mostrare.
     */
    public SettingsDialog(CryptoSplitter c) {
        this(c.getStartFile());

        passLabel.setEnabled(true);
        passValue.setEnabled(true);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);

        dimValue.setText(String.valueOf(c.getDimPar()));

        modValue.setSelectedIndex(1);
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file settato con ZipSplitter.
     * @param z ZipSplitter che sarà modificato, da cui prendo i dati da mostrare.
     */
    public SettingsDialog(ZipSplitter z){
        this(z.getStartFile());

        passLabel.setEnabled(false);
        passValue.setEnabled(false);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);

        dimValue.setText(String.valueOf(z.getDimPar()));

        modValue.setSelectedIndex(2);
    }

    /**
     * Metodo per ottenere il valore della ComboBox per la scelta della modalità.
     * @return L'istanza di ComboBox che rappresenta la selezione.
     */
    public JComboBox getModValue() {
        return modValue;
    }

    /**
     * Metodo che viene chiamato nel caso in cui venga chiuso positivamente il Dialog.
     */
    private void onOK() {
        dispose();
    }

    /**
     * Metodo che viene chiamato nel caso in cui venga chiuso negativamente il Dialog.
     */
    private void onCancel() {
        dispose();
    }

    /**
     * Metodo per ottenere la dimensione massima di ogni file nella divisone.
     * @return Il valore della dimensione massima di ogni file.
     */
    public JTextField getDimValue() {
        return dimValue;
    }

    /**
     * Metodo per ottenere la password che è stata inserita dall'utente.
     * @return L'oggetto che contiene la password inserita.
     */
    public JPasswordField getPassValue() {
        return passValue;
    }

    /**
     * Metodo per ottenere il numero massimo di parti in cui dividere il file.
     * @return Il componente che contiene il valore del numero di parti.
     */
    public JTextField getnPartiValue() {
        return nPartiValue;
    }
}