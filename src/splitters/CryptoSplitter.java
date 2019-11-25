package splitters;

import javax.crypto.*;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
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
        Thread splitter = new Thread(split);
        splitter.start();
        try {
            splitter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File attuale = new File(file.getName().substring(0, file.getName().lastIndexOf(".par")-1)+"1.par");
        int c = 1;

        while(attuale.exists()){
            KeyGenerator keygen = new KeyGenerator.getInstance("AES");
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
            attuale = new File(file.getName().substring(0, file.getName().lastIndexOf(".par")-1)+(++c)+".par");
        }
    }
}
