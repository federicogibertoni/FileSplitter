package splitters;

import java.io.File;

/**
 * Classe astratta da cui derivano tutti gli Splitter.
 */
public abstract class Splitter implements Runnable{
    protected File startFile;

    public Splitter(File f){
        startFile = f;
    }

    public Splitter(String path){
        startFile = new File(path);
    }

    public File getStartFile() {
        return startFile;
    }

    public void setStartFile(File startFile) {
        this.startFile = startFile;
    }

    abstract void split();
}
