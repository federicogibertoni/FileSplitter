import gui.MainPanel;

import javax.swing.JFrame;

/**
 * Classe principale che contiene il metodo {@link #main(String[])}  main()}.
 */
public class Main {

    /**
     * Metodo main() da cui parte l'esecuzione del programma.
     * Qui viene creata la finestra principale e vengono inizializzati i componenti.
     * @param args Argomenti passati da riga di comando.
     */
    public static void main(String[] args){
        JFrame jframe = new JFrame("FileSplitter");          //finestra principale
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //chiusura dell'applicazione quando si chiude la finestra
        jframe.setBounds(0, 0, 500, 500);    //dimensioni
        jframe.setLocationRelativeTo(null);                       //centrata
        jframe.add(new MainPanel());                              //creo il pannello principale
        jframe.setResizable(false);                               //disabilito il ridimensionamento
        jframe.setVisible(true);                                  //rendo visibile il frame
    }
}
