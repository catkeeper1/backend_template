package org.ckr.msdemo.doclet.model;

import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import org.ckr.msdemo.doclet.util.AnnotationScanTemplate;
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.ckr.msdemo.doclet.util.DocletUtil.logMsg;

/**
 * Created by Administrator on 2017/6/7.
 */
public class Table {


    public static final String TABLE_QUALIFIED_NAME = "javax.persistence.Table";
    public static final String TABLE_NAME = "name";
    public static final String TABLE_INDEXS = "indexes";


    private String tableName = null;

    private String packageName = null;

    private String className = null;

    private List<Index> indexList = new ArrayList<>();

    private List<Column> columnList = new ArrayList<>();

    private List<ForeignKey> foreignKeyList = new ArrayList<>();

    private String comment;

    private void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComment() {
        return comment;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    public String getTableName() {
        return tableName;
    }


    public List<Index> getIndexList() {
        return this.indexList;
    }

    public String getPackageName() {
        return packageName;
    }

    private void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFullClassName() {
        return this.getPackageName() + "." + this.getClassName();
    }

    public String getClassName() {
        return className;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    private void addIndex(Index index) {
        this.indexList.add(index);
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    private void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    public List<ForeignKey> getForeignKeyList() {
        return foreignKeyList;
    }

    private void setForeignKeyList(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    private Table() {

    }

    /**
     * Create Table according to classDoc.
     *
     * @param classDoc classDoc
     * @return Table
     */
    public static Table createEntity(ClassDoc classDoc) {


        Table instance = new Table();

        new AnnotationScanTemplate<Table>(classDoc, instance)
            .annotation(TABLE_QUALIFIED_NAME)
            .attribute(TABLE_NAME, (data, annotationValue) -> data.setTableName((String) annotationValue.value()))
            .attribute(TABLE_INDEXS, (data, annotationValue) ->
                createIndexList(data, (AnnotationValue[]) annotationValue.value()))
            .parent()
            .scanProgramElement();


        if (instance.tableName == null) {
            return null;
        }

        instance.setPackageName(DocletUtil.getPackageName(classDoc));

        instance.setClassName(classDoc.name());

        List<Column> columnList = Column.createColumns(classDoc);

        instance.setColumnList(columnList);

        instance.setComment(classDoc.commentText());

//        List<ForeignKey> foreignKeyList = ForeignKey.createForeignKeys(classDoc);
//
//        instance.setForeignKeyList(foreignKeyList);

        logMsg("create entity object for classDoc: " + classDoc);
        logMsg("table: " + instance);
        return instance;

    }

    private static void createIndexList(Table dataObject, AnnotationValue[] indexAnnotationList) {

        if (indexAnnotationList == null) {
            return;
        }

        for (AnnotationValue indexAnnotation : indexAnnotationList) {
            Index index = Index.createIndex(indexAnnotation);

            if (index != null) {
                dataObject.addIndex(index);
            }
        }

    }

    public static Table convertFromJoinTable(JoinTable joinTable, List<Table> existTables) {
        Table result = new Table();

        result.setTableName(joinTable.getTableName());


        Table joinedTable = null;

        for (Table table : existTables) {
            if (table.getFullClassName().equals(joinTable.getJoinFullClassName())) {
                joinedTable = table;
                break;
            }
        }

        if (joinedTable == null) {
            throw new RuntimeException("Cannot find joined table for:" + joinTable);
        }

        result.setPackageName(joinedTable.getPackageName());

        Table inversedTable = null;

        for (Table table : existTables) {
            if (table.getFullClassName().equals(joinTable.getInverseFullClassName())) {
                inversedTable = table;
                break;
            }
        }

        if (inversedTable == null) {
            throw new RuntimeException("Cannot find inversed table for:" + joinTable);
        }

        List<Column> resultColumnList = new ArrayList<>();

        for (Column joinColumn : joinTable.getJoinColumnList()) {

            for (Column column : joinedTable.getColumnList()) {
                if (joinColumn.getName().equals(column.getName())) {
                    resultColumnList.add(column);
                }
            }

        }

        for (Column invesColumn : joinTable.getInverseColumnList()) {

            for (Column column : inversedTable.getColumnList()) {
                if (invesColumn.getName().equals(column.getName())) {
                    resultColumnList.add(column);
                }
            }

        }


        result.setClassName("");

        result.setColumnList(resultColumnList);

        DocletUtil.logMsg("converted table:" + result.toString());

        return result;
    }

    public void setForeignKeyInfo(Map<String, List<ForeignKey>> foreignKeyMap) {
        List<ForeignKey> fkList = foreignKeyMap.get(this.tableName);

        if(fkList == null) {
            return;
        }
        this.setForeignKeyList(fkList);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("tableName='").append(tableName).append('\'');
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append(", className='").append(className).append('\'');
        sb.append(", indexList=").append(indexList);
        sb.append(", columnList=").append(columnList);
        sb.append(", foreignKeyList=").append(foreignKeyList);
        sb.append(", comment='").append(comment).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(tableName, table.tableName) &&
                Objects.equals(packageName, table.packageName) &&
                Objects.equals(className, table.className) &&
                Objects.equals(indexList, table.indexList) &&
                Objects.equals(columnList, table.columnList) &&
                Objects.equals(foreignKeyList, table.foreignKeyList) &&
                Objects.equals(comment, table.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, packageName, className, indexList, columnList, foreignKeyList, comment);
    }
}
