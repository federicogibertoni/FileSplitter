package gui;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.ZipSplitter;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import static utils.Const.FIELD_ERROR_MESSAGE;
import static utils.Const.TITLE_FIELD_ERROR_MESSAGE;

/**
 * Classe che implementa il Dialog che viene aperto quando si aggiunge un nuovo file alla coda oppure se si vogliono modificare le impostazioni.
 * Sottoclasse di {@link JDialog JDialog}
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
     * Bottone per annullare l'inserimento chiudendo il dialog.
     */
    private JButton buttonCancel;
    /**
     * ComboBox che permette di effettuare la scelta del tipo di divisione da effettuare.
     * @see JComboBox
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
     * Label che rappresenta dove andrà il file diviso.
     */
    private JLabel dirLabel;
    /**
     * Bottone che permette di aprire un JFileChooser per scegliere la directory.
     * @see JFileChooser
     */
    private JButton dirValue;
    /**
     * Valore booleano che permette di capire se è andata a buon fine o meno la compilazione dei campi.
     */
    private boolean state;

    /**
     * Classe interna che implementa il {@link ActionListener listener} per animare i campi del dialog a seconda della selezione della JComboBox.
     */
    private class ComboSelectionListener implements ActionListener{

        /**
         * Implementazione del Listener.
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
     * Listener per il bottone che fa scegliere dove andare a dividere i file.
     */
    private class NavigaButtonListener implements ActionListener{

        /**
         * Una volta premuto il bottone viene aperto un JFileChooser per poter scegliere la cartella di destinazione.
         * @param e Evento generato dal click sul bottone.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser dirChooser = new JFileChooser();
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(dirChooser.showDialog(getParent(), "Apri") == JFileChooser.APPROVE_OPTION) {
                try {
                    dirLabel.setText(dirChooser.getSelectedFile().getCanonicalPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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

        dirLabel = new JLabel("Directory");
        dirValue = new JButton("Naviga");
        dirValue.addActionListener(new NavigaButtonListener());
    }

    /**
     * Metodo privato che imposta il layout per tutto il Dialog.
     * Fa uso di un {@link GroupLayout GroupLayout}.
     */
    private void setDialogLayout(){
        GroupLayout groupLayout = new GroupLayout(contentPane);

        contentPane.setLayout(groupLayout);

        groupLayout.setHorizontalGroup(             //devo creare un gruppo per creare delle "colonne" virtuali
                groupLayout.createSequentialGroup()
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(modLabel)
                                .addComponent(dimLabel)
                                .addComponent(passLabel)
                                .addComponent(nPartiLabel)
                                .addComponent(dirLabel)
                                .addComponent(buttonOK)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(fileName)
                                .addComponent(modValue)
                                .addComponent(dimValue)
                                .addComponent(passValue)
                                .addComponent(nPartiValue)
                                .addComponent(dirValue)
                                .addComponent(buttonCancel)
                )
        );

        groupLayout.setVerticalGroup(                   //devo creare un gruppo per creare delle "righe" virtuali
                groupLayout.createSequentialGroup()
                .addComponent(fileName)
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(modLabel)
                            .addComponent(modValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(dimLabel)
                            .addComponent(dimValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(passLabel)
                            .addComponent(passValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(nPartiLabel)
                            .addComponent(nPartiValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(dirLabel)
                            .addComponent(dirValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                           .addComponent(buttonOK)
                           .addComponent(buttonCancel)
                )
        );

        groupLayout.setAutoCreateGaps(true);                    //spaziatura tra i componenti
        groupLayout.setAutoCreateContainerGaps(true);           //non far toccare i bordi
    }

    /**
     * Costruttore chiamato durante l'inserimento di un nuovo valore nella tabella.
     * @param att File che è elaborato attualmente.
     */
    public SettingsDialog(File att) {
        super();

        initComponents();
        setDialogLayout();

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
        dirLabel.setText(att.getParent());
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file associato ad un {@link BufferedSplitter BufferedSplitter}.
     * @param tmp BufferedSplitter che sarà modificato, da cui prendo i dati da mostrare.
     */
    public SettingsDialog(BufferedSplitter tmp) {
        this(tmp.getStartFile());

        passLabel.setEnabled(false);
        passValue.setEnabled(false);

        //decido che campi abilitare e imposto i vecchi valori
        if(!tmp.isParti()){
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
        dirLabel.setText(tmp.getFinalDirectory());
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file associato ad un {@link CryptoSplitter CryptoSplitter}.
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

        dirLabel.setText(c.getFinalDirectory());
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file associato ad un {@link ZipSplitter ZipSplitter}.
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

        dirLabel.setText(z.getFinalDirectory());
    }

    /**
     * Metodo per ottenere il valore della ComboBox usata per la scelta della modalità di divisione.
     * @return L'istanza di ComboBox che rappresenta la selezione.
     */
    public JComboBox getModValue() {
        return modValue;
    }

    /**
     * Metodo che viene chiamato nel caso in cui venga chiuso positivamente il Dialog.
     * Viene controllata la corretta compilazione con {@link #validateFields() validateFields()}.
     */
    private void onOK() {
        if(validateFields()) {
            state = true;
            dispose();
        }
        else
            state = false;
    }

    /**
     * Metodo che viene chiamato nel caso in cui venga chiuso negativamente il Dialog.
     */
    private void onCancel() {
        state = false;
        dispose();
    }

    /**
     * Metodo privato per controllare che i campi richiesti dal dialog siano tutti compilati alla sua chiusura.
     * @return true se i campi sono tutti completati, false altrimenti.
     */
    private boolean validateFields() {
        switch (getModValue().getSelectedIndex()){
            case 0: //se è la divisione "classica"
            case 2: //se è la divisione con compressione
                if(getDimValue().getText().length() == 0) {
                    JOptionPane.showMessageDialog(this, FIELD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case 1: //se è la divisione con cifratura
                if(getDimValue().getText().length() == 0 || getPassValue().getPassword().length == 0) {
                    JOptionPane.showMessageDialog(this, FIELD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case 3: //se è la divisione con numero di parti
                if(getnPartiValue().getText().length() == 0) {
                    JOptionPane.showMessageDialog(this, FIELD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
        }

        return true;
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

    /**
     * Metodo per ottenere la directory dove andrà il file diviso, contenuta nel testo della JLabel.
     * @return JLabel contenente la directory dove andrà il file diviso.
     */
    public JLabel getDirLabel() {
        return dirLabel;
    }

    /**
     * Metodo che ritorna lo stato del dialog alla chiusura e dopo aver controllato la completezza dei suoi campi.
     * @return true se è stato compilato correttamente, false altrimenti.
     */
    public boolean getState() {
        return state;
    }
}