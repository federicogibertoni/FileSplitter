package mergers;

import static utils.MyUtils.MD5;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Merger {
    private File startFile;

    public Merger(String path) {
        startFile = new File(path);
    }

    public Merger(File f) {
        startFile = f;
    }

    public void merge() {
        String ext = startFile.getName().substring(startFile.getName().lastIndexOf(".par") + (".par".length()), (int) startFile.getName().length());
        switch (ext) {
            case ".crypto":
                decript();
                break;
            case ".zip":
                unzip();
                break;
            default:
                simpleMerge();
                break;
        }
    }

    private void unzip() {
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par") - 1);
        String nomeFileFinale = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par") - 1) + "fine";

        int c = 1, dimBuf = 8192;
        byte[] buf = new byte[dimBuf];

        File out = new File(nomeFileFinale);

        ZipInputStream zis = null;
        FileOutputStream fos = null;
        ZipEntry ze = null;

        try {
            zis = new ZipInputStream(new FileInputStream(startFile.getName()));

            if (!out.exists())
                out.createNewFile();
            fos = new FileOutputStream(out);
            //ze = zis.getNextEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File attuale = startFile;       //file attuale da cui iniziare a leggere
        while (attuale.exists()) {      //finché non ha ancora letto l'ultima parte
            try {
                while((ze = zis.getNextEntry()) != null){
                    while (zis.available() > 0) {
                        zis.read(buf);
                        fos.write(buf);                     //scrivi
                    }
                }
            } catch (IOException e) {
                    e.printStackTrace();
            }

        }

        attuale = new File(nomeFile + (++c) + ".par.zip"); //cambia l'input da cui leggere
        try {
            if (attuale.exists()) {
                zis.close();
                zis = new ZipInputStream(new FileInputStream(attuale));     //crea il nuovo stream
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            zis.close();
            fos.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void decript() {
        System.out.println("Inserisci la chiave per decriptare");
        String chiaveString = null;
        byte[] digestedPass;
        try {
            chiaveString = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        digestedPass = MD5(chiaveString);
        Key key = new SecretKeySpec(digestedPass, 0, digestedPass.length, "AES");

        byte[] iv = new byte[16];
        File attuale = startFile;
        FileInputStream fis = null;

        try{
            fis = new FileInputStream(attuale);
            fis.read(iv);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1);
        int dimBuf = 8192, c = 1;
        byte[] buf = new byte[dimBuf];
        CipherInputStream cis = null;
        FileOutputStream fos = null;
        try {
            cis = new CipherInputStream(fis, cipher);
            fos = new FileOutputStream(new File(startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1) + "fine"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(attuale.exists()) {
            //while (true) {
                try {
                    while ((cis.read(buf) != -1))
                        fos.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
            attuale = new File(nomeFile + (++c) + ".par.crypto");
            try {
                if (attuale.exists()) {
                    cis.close();
                    cis = new CipherInputStream(new FileInputStream(attuale), cipher);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        File attuale = startFile;
//        int c = 1;
//        while(attuale.exists()){
//            try {
//                cipher.doFinal(Files.readAllBytes(attuale.toPath()));
//            } catch (IllegalBlockSizeException e) {
//                e.printStackTrace();
//            } catch (BadPaddingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            attuale.renameTo(new File(attuale.getName().substring(0, attuale.getName().lastIndexOf(".par")+".par".length())));
//            attuale = new File(attuale.getName().substring(0, (attuale.getName().lastIndexOf(".par")-(String.valueOf(c).length())))+(++c)+".par.crypto");
//        }
    }

    private void simpleMerge() {
        String nomeFile = startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1);
        String nomeFileFinale = //"\\"+"finale"+/*File.separator*/ "\\"+
                startFile.getName().substring(0, startFile.getName().lastIndexOf(".par")-1) + "fine";

        int c = 1, dimBuf = 8192;
        byte[] buf = new byte[dimBuf];

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
                    fis.read(buf);
                    output.write(buf);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            attuale = new File(nomeFile+(++c)+".par");
            try {
                if(attuale.exists()) {
                    fis.close();
                    fis = new FileInputStream(attuale);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}