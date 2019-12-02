package splitters;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipSplitter implements Runnable {
    private File startFile;

    public ZipSplitter(File f){
        startFile = f;
    }

    public ZipSplitter(String path){
        startFile = new File(path);
    }

    @Override
    public void run() {
        assert startFile.exists();

        String outputFile = startFile.getName()+"1.par";

        int trasf = (int) startFile.length(), c = 1, i = 0, dimBuf = 8192, dimPar = 2097152;

        byte[] buf = new byte[dimBuf];

        FileInputStream fis = null;
        ZipOutputStream zos = null;

        try{
            fis = new FileInputStream(startFile);
            zos = new ZipOutputStream(new FileOutputStream(outputFile+".zip"));

            zos.putNextEntry(new ZipEntry(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                zos.write(buf);
                dimPar -= dimBuf;
                if(dimPar <= 0){
                    dimPar = 2097152;
                    zos.closeEntry();
                    zos.flush();
                    zos.close();
                    zos = new ZipOutputStream(new FileOutputStream(startFile.getName() + "" + (++c) + ".par.zip"));
                    zos.putNextEntry(new ZipEntry(startFile.getName() + "" + (c) + ".par"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try{
            fis.close();
            zos.closeEntry();
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                //trasf -= dimBuf;
                fos.write(buf);
                dimPar -= dimBuf;
                if(/*trasf <= 0 || dimPar <= 0) {
                    dimPar = 104857600;
                    fos.close();
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
        }*/
    }
}
