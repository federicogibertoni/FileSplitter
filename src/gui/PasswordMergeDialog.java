package gui;

import javax.swing.*;
import java.awt.event.*;

import static utils.Const.FIELD_ERROR_MESSAGE;
import static utils.Const.TITLE_FIELD_ERROR_MESSAGE;

/**
 * Classe che implementa il Dialog che sarà aperto quando viene fatto il merge di un file criptato per chiedere la password.
 * Sottoclasse di {@link JDialog JDialog}
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
     * Valore booleano che permette di capire se è andata a buon fine o meno la compilazione dei campi.
     */
    private boolean state;

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
     * Metodo privato che imposta il layout per tutto il dialog.
     * Fa uso di un {@link GroupLayout GroupLayout}.
     */
    private void setDialogLayout(){
        GroupLayout groupLayout = new GroupLayout(contentPane);

        contentPane.setLayout(groupLayout);

        //creo il layout orizzontale
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(passLabel)
                        .addComponent(buttonOK)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(passValue)
                        .addComponent(buttonCancel)
                )
        );

        //creo il layout verticale
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passLabel)
                        .addComponent(passValue)
                )
                .addGroup(
                        groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(buttonOK)
                        .addComponent(buttonCancel)
                )
        );

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
    }

    /**
     * Costruttore del Dialog che viene chiamato quando bisogna riunire i pezzi di file criptati.
     */
    public PasswordMergeDialog() {
        super();

        initComponents();
        setDialogLayout();

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

        //quando si esce dalla finestra
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        //quando si preme ESC
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * Metodo che viene chiamato nel caso in cui venga chiuso positivamente il Dialog.
     * Viene controllata la corretta compilazione con {@link #validateField() validateField()}.
     */
    private void onOK() {
        if(validateField()) {
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
     * Metodo privato per controllare che la password sia stata inserita alla sua chiusura.
     * @return true se è stata inserita la password, false altrimenti.
     */
    private boolean validateField(){
        if (getPassValue().getPassword().length == 0){
            JOptionPane.showMessageDialog(this, FIELD_ERROR_MESSAGE, TITLE_FIELD_ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Metodo che ritorna lo stato del JDialog alla chiusura e dopo aver controllato la completezza del campo.
     * @return true se è stato compilato correttamente, false altrimenti.
     */
    public boolean getState() {
        return state;
    }

    /**
     * Metodo che ritorna il componente che contiene la password inserita.
     * @return Il componente che contiene la password con cui decriptare i file.
     */
    public JPasswordField getPassValue() {
        return passValue;
    }
}