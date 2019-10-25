package org.ckr.msdemo.doclet;

import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import org.ckr.msdemo.doclet.model.DataModel;
import org.ckr.msdemo.doclet.util.DocletUtil;
import org.ckr.msdemo.doclet.writter.ErDiagramWriter;

/**
 * Created by Administrator on 2017/11/11.
 */
public class ErDiagramDoclet {

    public static boolean start(RootDoc root) {


        DataModel dataModel = new DataModel(root.classes());

        ErDiagramWriter writter = new ErDiagramWriter(DocletUtil.getOutputDirPath(), dataModel.getTableList());

        writter.generateErDiagram();

        return true;
    }

    /**
     * Specify the JAVA version.
     * If 1.5 is not specified here, some annotation will no be parsed by javadoc so that the Doclet
     * also cannot read those annotation info.
     * @return LanguageVersion
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

}
