package splitters;

import static utils.MyUtils.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;

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
        FileOutputStream fos = null;
        Key key = null;

        System.out.println("Inserisci una password per criptare");
        String pass = null;
        byte[] digestedPass;
        try {
            pass = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        digestedPass = MD5(pass);
        key = new SecretKeySpec(digestedPass,0,digestedPass.length, "AES");

        SecureRandom srGen = new SecureRandom();
        byte[] iv = new byte[16];
        srGen.nextBytes(iv);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getName()+""+"1.par.crypto");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int trasf = (int) file.length(), c = 1, i = 0, dimBuf = 8192, dimPar = 104857600;
        byte[] buf = new byte[dimBuf];
        try {
            fos.write(iv);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        while(true){
            try {
                if (!(fis.read(buf) != -1)) break;
                //trasf -= dimBuf;
                cos.write(buf);
                dimPar -= dimBuf;
                if(/*trasf <= 0 || */dimPar <= 0) {
                    dimPar = 104857600;
                    cos.flush();
                    cos.close();
                    cos = new CipherOutputStream(new FileOutputStream(file.getName() + "" + (++c) + ".par.crypto"), cipher);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fis.close();
            cos.flush();
            cos.close();
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