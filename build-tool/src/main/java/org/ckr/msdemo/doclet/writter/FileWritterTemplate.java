package org.ckr.msdemo.doclet.writter;

import org.ckr.msdemo.doclet.exception.DocletException;
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Template for Liquibase file generation.
 */
public abstract class FileWritterTemplate {

    private File file = null;

    public FileWritterTemplate(File file) {
        this.file = file;
    }

    protected abstract void doWrite(OutputStreamWriter writer) throws IOException;

    /**
     * Template steps for Liquibase file generation.
     */
    public void execute() {



        try (FileWriter docWriter = new FileWriter(file)) {

            DocletUtil.logMsg("open file for writing: " + file.getAbsolutePath());
            this.doWrite(docWriter);
            docWriter.flush();
            DocletUtil.logMsg("flush file: " + file.getAbsolutePath());
        } catch (IOException ioExp) {
            throw new DocletException("cannot write file.", ioExp);
        }

    }


}
