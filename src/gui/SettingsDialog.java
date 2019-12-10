package gui;

import javax.swing.*;
import java.awt.event.*;

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

    private class ComboSelectionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (modValue.getSelectedIndex()){
                case 0:
                    dimLabel.setEnabled(true);
                    dimValue.setEnabled(true);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 1:
                    dimLabel.setEnabled(false);
                    dimValue.setEnabled(false);
                    passLabel.setEnabled(true);
                    passValue.setEnabled(true);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 2:
                    dimLabel.setEnabled(false);
                    dimValue.setEnabled(false);
                    passLabel.setEnabled(false);
                    passValue.setEnabled(false);
                    nPartiLabel.setEnabled(false);
                    nPartiValue.setEnabled(false);
                    break;
                case 3:
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

    public SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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


        modValue.addActionListener(new ComboSelectionListener());

        dimLabel.setEnabled(true);
        dimValue.setEnabled(true);
        passLabel.setEnabled(false);
        passValue.setEnabled(false);
        nPartiLabel.setEnabled(false);
        nPartiValue.setEnabled(false);
    }

    public JComboBox getModValue() {
        return modValue;
    }

    public void setModValue(JComboBox modValue) {
        this.modValue = modValue;
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SettingsDialog dialog = new SettingsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
