package view;

import splitters.Splitter;

import javax.swing.*;

public class RowElement {
    private Splitter task;
    private JComboBox mod;
    private JProgressBar progress;

    public RowElement(Splitter task, JComboBox mod, JProgressBar progress) {
        this.task = task;
        this.mod = mod;
        this.progress = progress;
    }

    public JComboBox getMod() {
        return mod;
    }

    public void setMod(JComboBox mod) {
        this.mod = mod;
    }

    public Splitter getTask() {
        return task;
    }

    public void setTask(Splitter task) {
        this.task = task;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JProgressBar progress) {
        this.progress = progress;
    }
}
