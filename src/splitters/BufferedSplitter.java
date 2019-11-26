package splitters;

import java.io.*;

public class BufferedSplitter implements Runnable {
    private File file;

    public BufferedSplitter(String path){
        file = new File(path);
    }

    public BufferedSplitter(File f){
        file = f;
    }

    @Override
    public void run() {
        assert file.exists();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getName()+""+"1.par");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) file.length(), c = 1, i = 0, dimBuf = 8192, dimPar = 104857600;
        byte[] buf = new byte[dimBuf];

        //for(int i = 0; i<file.length(); i++){
        while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                //trasf -= dimBuf;
                fos.write(buf);
                dimPar -= dimBuf;
                if(/*trasf <= 0 || */dimPar <= 0) {
                    dimPar = 104857600;
                    fos = new FileOutputStream(file.getName() + "" + (++c) + ".par");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
