package gui;

import javax.swing.*;
import java.awt.event.*;

/**
 * Classe che implementa il Dialog che sarà aperto quando viene fatto il merge di un file criptato per chiedere la password.
 */
public class PasswordMergeDialog extends JDialog {
    /**
     * Pannello che contiene il Dialog.
     */
    private JPanel contentPane;
    /**
     * Bottone per dare la conferma della password inserita.
     */
    private JButton buttonOK;
    /**
     * Bottone per annullare l'inserimento della password.
     */
    private JButton buttonCancel;
    /**
     * Campo di testo che rappresenta il valore della nuova password inserita.
     */
    private JPasswordField passValue;
    /**
     * Label che indica il campo di testo dove andrà la password.
     */
    private JLabel passLabel;

    /**
     * Metodo privato per creare delle istanze di ogni componente del Dialog.
     */
    private void initComponents(){
        contentPane = new JPanel();
        contentPane.setOpaque(true);
        this.setContentPane(contentPane);

        setModal(true);

        buttonCancel = new JButton("Annulla");
        buttonOK = new JButton("Ok");
        getRootPane().setDefaultButton(buttonOK);  //nel pannello di base metto come bottone di default di chiusura quello di OK

        passLabel = new JLabel("Password");
        passValue = new JPasswordField();
        passValue.setColumns(30);
    }

    /**
     * Metodo privato per aggiungere tutte le istanze create al pannello del Dialog.
     */
    private void addComponents(){
        add(passLabel);
        add(passValue);

        add(buttonOK);
        add(buttonCancel);
    }

    /**
     * Costruttore del Dialog che viene chiamato quando bisogna riunire i pezzi di file criptati.
     */
    public PasswordMergeDialog() {
        super();

        initComponents();
        addComponents();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
     * Metodo che ritorna il componente che contiene la password inserita.
     * @return Il componente che contiene la password con cui decriptare i file.
     */
    public JPasswordField getPassValue() {
        return passValue;
    }
}