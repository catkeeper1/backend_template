package org.ckr.msdemo.doclet.model;

import com.sun.javadoc.ClassDoc;//NOSONAR
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ruoli.chen on 05/06/2017.
 */
public class DataModel {



    private List<Table> tableList = new ArrayList<>();

    /**
     * DataModel Contractor to extract table list from annotated classes.
     *
     * @param classeDocs classeDocs
     */
    public DataModel(ClassDoc[] classeDocs) {


        for (int i = 0; i < classeDocs.length; i++) {

            Table table = Table.createEntity(classeDocs[i]);
            if (table == null) {
                continue;
            }
            tableList.add(table);
        }

        if (tableList.isEmpty()) {
            return;
        }

        Map<String, List<ForeignKey>> foreignKeyMap = new HashMap<>();

        for (int i = 0; i < classeDocs.length; i++) {
            foreignKeyMap.putAll(ForeignKey.createForeignKeys(classeDocs[i], tableList));
        }

        for(Table table : tableList) {

            table.setForeignKeyInfo(foreignKeyMap);

        }





        DocletUtil.logMsg("final table data:");
        for(Table table : tableList) {

            DocletUtil.logMsg(table.toString());

        }
    }

    public List<Table> getTableList() {
        return tableList;
    }
}
