package org.ckr.msdemo.doclet;

import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.ckr.msdemo.doclet.model.DataModel;
import org.ckr.msdemo.doclet.util.DocletUtil;
import org.ckr.msdemo.doclet.writter.LiquibaseWriter;

/**
 * Doclet to generate Liquibase config file.
 */
public class LiquiBaseDoclet {

    /**
     * Entry to start generate Liquibase config file.
     *
     * @param root RootDoc
     * @return true
     */
    public static boolean start(RootDoc root) {


        DataModel dataModel = new DataModel(root.classes());

        LiquibaseWriter writter = new LiquibaseWriter(DocletUtil.getOutputDirPath(), dataModel);

        writter.generateDdlXmlConfigDoc();
        writter.generateInsertXmlConfigDoc();
        writter.generateInsertCsvTemplate();
        writter.generateIncludeXmlConfig();

        return true;
    }

    /**
     * Specify the JAVA version.
     * If 1.5 is not specified here, some annotation will no be parsed by javadoc so that the Doclet
     * also cannot read those annotation info.
     *
     * @return LanguageVersion
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
