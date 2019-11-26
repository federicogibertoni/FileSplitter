package splitters;

import javax.crypto.*;

import java.io.*;

import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoSplitter implements Runnable {
    private BufferedSplitter split;

    private File file;

    public CryptoSplitter(String path){
        file = new File(path);
        split = new BufferedSplitter(path);
    }

    public CryptoSplitter(File f){
        file = f;
        split = new BufferedSplitter(file);
    }

    @Override
    public void run() {
        assert file.exists();

        FileInputStream fis = null;
        CipherOutputStream fos = null;
        Key key = null;

        try {
            key = KeyGenerator.getInstance("AES").generateKey();
            File chiave = new File("chiave.txt");
            if(!chiave.exists())
                chiave.createNewFile();
            FileOutputStream fosk = new FileOutputStream(chiave);
            fosk.write(key.getEncoded());
            fosk.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            fis = new FileInputStream(file);
            fos = new CipherOutputStream(new FileOutputStream(file.getName()+""+"1.par.crypto"), cipher);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) file.length(), c = 1, i = 0, dimBuf = 8192, dimPar = 104857600;
        byte[] buf = new byte[dimBuf];

        while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                //trasf -= dimBuf;
                fos.write(buf);
                dimPar -= dimBuf;
                if(/*trasf <= 0 || */dimPar <= 0) {
                    dimPar = 104857600;
                    fos = new CipherOutputStream(new FileOutputStream(file.getName() + "" + (++c) + ".par.crypto"), cipher);
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

/*Thread splitter = new Thread(split);
        splitter.start();
        try {
            splitter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File attuale = new File(file.getName()+"1.par");
        int c = 1;

        while(attuale.exists()){
            KeyGenerator keygen = null;
            try {
                keygen = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            keygen.init(128);

            Key key = keygen.generateKey();

            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                cipher.doFinal(Files.readAllBytes(attuale.toPath()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            attuale.renameTo(new File(attuale.getName()+".crypto"));
            attuale = new File(attuale.getName().substring(0, attuale.getName().lastIndexOf(".par")-1)+(++c)+".par");
        }*/