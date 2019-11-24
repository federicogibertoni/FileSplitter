package splitters;

import java.io.*;

public class Splitter implements Runnable{
    private File file;

    public Splitter(String path){
        file = new File(path);
    }

    public Splitter(File f){
        file = f;
    }

    @Override
    public void run() {
        int dim = 10240;
        assert file.exists();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf, c = 0;

        for(int i = 0; i<file.length(); i++){
            try {
                if(i == 0 || i % dim == 0)
                    fos = new FileOutputStream(file.getName()+""+(++c)+".par");
                trasf = fis.read();
                fos.write(trasf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
