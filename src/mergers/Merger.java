package mergers;

import javax.swing.*;
import java.io.*;

public class Merger {
    private File startFile;

    public Merger(String path){
        startFile = new File(path);
    }

    public Merger(File f){
        startFile = f;
    }

    public void merge() {
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1);
        String nomeFileFinale = //"\\"+"finale"+/*File.pathSeparator*/ "\\"+
                startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1) + "fine";

        int c = 1;

        FileOutputStream output = null;
        File attuale = startFile, out = new File(nomeFileFinale);
        FileInputStream fis = null;
        try {
            if (!out.exists())
                out.createNewFile();
            output = new FileOutputStream(out);
            fis = new FileInputStream(attuale);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(attuale.exists()){
            while(true){
                try {
                    if (!(fis.available() != 0)) break;
                    output.write(fis.read());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            attuale = new File(nomeFile+(++c)+".par");
            try {
                if(attuale.exists())
                    fis = new FileInputStream(attuale);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
