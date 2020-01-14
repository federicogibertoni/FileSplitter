import gui.MainPanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        JFrame jframe = new JFrame("FileSplitter");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setBounds(0, 0, 500, 500);
        jframe.setLocationRelativeTo(null);
        jframe.add(new MainPanel());
        jframe.setResizable(false);
        jframe.setVisible(true);
    }
}
