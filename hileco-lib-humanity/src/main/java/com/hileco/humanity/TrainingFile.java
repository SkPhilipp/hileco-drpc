package com.hileco.humanity;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.Consumer;

public class TrainingFile implements Consumer<String> {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingFile.class);

    private File file;

    public TrainingFile(File file) {
        this.file = file;
    }

    @Override
    public synchronized void accept(String input) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(input.getBytes(Charsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public void drain(Consumer<String> trainable) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(fileInputStream, Charsets.UTF_8));
            String line;
            while ((line = lineReader.readLine()) != null) {
                trainable.accept(line);
            }
            fileInputStream.close();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

}
