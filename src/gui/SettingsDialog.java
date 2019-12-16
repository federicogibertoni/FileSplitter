package gui;

import splitters.BufferedSplitter;
import splitters.CryptoSplitter;
import splitters.ZipSplitter;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;


//
//METTERE IL NOME DEL FILE CHE SI STA MODIFICANDO O AGGIUNGENDO IN ALTO
//


public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox modValue;
    private JTextField dimValue;
    private JPasswordField passValue;
    private JTextField nPartiValue;
    private JLabel dimLabel;
    private JLabel passLabel;
    private JLabel nPartiLabel;
    private JLabel modLabel;
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
                    dimLabel.setEnabled(true);
                    dimValue.setEnabled(true);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 1:         //CryptoSplitter
                    dimLabel.setEnabled(false);
                    dimValue.setEnabled(false);
                    passLabel.setEnabled(true);
                    passValue.setEnabled(true);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 2:         //ZipSplitter
                    dimLabel.setEnabled(false);
                    dimValue.setEnabled(false);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
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
     * Costruttore chiamato durante l'inserimento di un nuovo valore nella tabella.
     * @param att File che è elaborato attualmente.
     */
    public SettingsDialog(File att) {
        setContentPane(contentPane);
        setModal(true);             //blocca l'input nelle altre finestre
        getRootPane().setDefaultButton(buttonOK);       //nel pannello di base metto come bottone di default di chiusura quello di OK

        //aggiungo listener ai bottoni
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

        dimLabel.setEnabled(false);
        dimValue.setEnabled(false);
        passLabel.setEnabled(true);
        passValue.setEnabled(true);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);

        modValue.setSelectedIndex(1);
    }

    /**
     * Costruttore del dialog chiamato in fase di modifica di un file settato con ZipSplitter.
     * @param z ZipSplitter che sarà modificato, da cui prendo i dati da mostrare.
     */
    public SettingsDialog(ZipSplitter z){
        this(z.getStartFile());

        dimLabel.setEnabled(false);
        dimValue.setEnabled(false);
        passLabel.setEnabled(false);
        passValue.setEnabled(false);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);

        modValue.setSelectedIndex(2);
    }


    //metodi get e set

    public JComboBox getModValue() {
        return modValue;
    }

    public void setModValue(JComboBox modValue) {
        this.modValue = modValue;
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public JTextField getDimValue() {
        return dimValue;
    }

    public void setDimValue(JTextField dimValue) {
        this.dimValue = dimValue;
    }

    public JPasswordField getPassValue() {
        return passValue;
    }

    public void setPassValue(JPasswordField passValue) {
        this.passValue = passValue;
    }

    public JTextField getnPartiValue() {
        return nPartiValue;
    }

    public void setnPartiValue(JTextField nPartiValue) {
        this.nPartiValue = nPartiValue;
    }

    public JLabel getDimLabel() {
        return dimLabel;
    }

    public void setDimLabel(JLabel dimLabel) {
        this.dimLabel = dimLabel;
    }

    public JLabel getPassLabel() {
        return passLabel;
    }

    public void setPassLabel(JLabel passLabel) {
        this.passLabel = passLabel;
    }

    public JLabel getnPartiLabel() {
        return nPartiLabel;
    }

    public void setnPartiLabel(JLabel nPartiLabel) {
        this.nPartiLabel = nPartiLabel;
    }

    public JLabel getModLabel() {
        return modLabel;
    }

    public void setModLabel(JLabel modLabel) {
        this.modLabel = modLabel;
    }
}
