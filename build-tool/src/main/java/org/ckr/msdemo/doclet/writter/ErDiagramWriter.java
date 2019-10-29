package org.ckr.msdemo.doclet.writter;

import org.ckr.msdemo.doclet.exception.DocletException;
import org.ckr.msdemo.doclet.model.Column;
import org.ckr.msdemo.doclet.model.ForeignKey;
import org.ckr.msdemo.doclet.model.Table;
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;

import static org.ckr.msdemo.doclet.util.DocletUtil.indent;
import static org.ckr.msdemo.doclet.util.DocletUtil.logMsg;

/**
 * Created by Administrator on 2017/11/11.
 */
public class ErDiagramWriter {

    //the file for the generated ER diagram.
    private File outputFile;

    private List<Table> tableList;

    public ErDiagramWriter(String outputFilePath, List<Table> tableList) {

        this.tableList = tableList;

        createOutputFile(outputFilePath);
    }

    protected void createOutputFile(String outputFilePath) {
        File file = new File(outputFilePath);

        if (file.isDirectory()) {
            throw new DocletException(file.getAbsolutePath() + " is not a directory.");
        }

        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if(!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                if(!file.createNewFile()) {
                    throw new DocletException("cannot create file " + file.getAbsolutePath());
                }

            } catch (IOException e) {

                throw new DocletException("cannot create file " + file.getAbsolutePath(), e);
            }
        }

        this.outputFile = file;
    }

    public void generateErDiagram() {
        final ErDiagramWriter t = this;
        new FileWritterTemplate(this.outputFile) {

            @Override
            protected void doWrite(OutputStreamWriter writer) throws IOException {
                t.writeHeader(writer);
                t.writeEntities(writer);
                t.writeFooter(writer);
            }
        }.execute();

    }

    private void writeHeader(OutputStreamWriter writer) throws IOException {

        writer.write("@startuml" + DocletUtil.ENTER);
        writer.write(DocletUtil.ENTER);
        writer.write("hide circle" + DocletUtil.ENTER);
        writer.write("hide empty members" + DocletUtil.ENTER);

    }

    private void writeEntities(OutputStreamWriter writer) throws IOException {
        for(Table table: tableList) {

            if (table.getColumnList() == null || table.getColumnList().isEmpty()) {
                logMsg("no column so that skip table " + table.getTableName());
                continue;
            }

            logMsg("write entity for table " + table.getTableName());
            writer.write("entity " + table.getTableName() + " {" + DocletUtil.ENTER);
            writeColumns(writer, table);
            writer.write("}" + DocletUtil.ENTER);
            writer.write(DocletUtil.ENTER);
        }

        for(Table table: tableList) {
            if (table.getForeignKeyList() == null || table.getForeignKeyList().isEmpty()) {
                logMsg("no FK so that skip table " + table.getTableName());
                continue;
            }

            writeRelationShip(writer, table);

        }
    }

    private void writeColumns(OutputStreamWriter writer, Table table) throws IOException {

        logMsg("write columns for table " + table.getTableName());
        int columnIndex = 1;
        for(Column column : table.getColumnList()) {


            logMsg("write column " + column.getName());
            String columnDef = DocletUtil.getColumnTypeForErDiagram(column);
            DecimalFormat format = new DecimalFormat("000");

            String columnName = column.getName();

            if(Boolean.TRUE.equals(column.getIsPrimaryKey())) {
                columnName = "**" + column.getName() + "**";
            }

            writer.write(indent(1) + format.format(columnIndex) + "." +
                    columnName +
                    " : " + columnDef + " ");

            writer.write("[[[../apidocs/");
            writer.write(table.getFullClassName().replace('.', '/'));
            writer.write(".html");

            String getMethodName = column.getJavaFieldName();
            if(getMethodName != null && getMethodName.length() > 0) {
                getMethodName = "get" + getMethodName.substring(0, 1).toUpperCase() + getMethodName.substring(1);
            }

            if(getMethodName != null) {
                writer.write("#" + getMethodName + "--");
            }
            writer.write("]]]" + DocletUtil.ENTER);
            columnIndex++;

        }



    }

    private boolean validateForeignKey (ForeignKey foreignKey) {
        boolean invalid = false;
        if(foreignKey.getSourceColumnNames() == null ||
                foreignKey.getSourceColumnNames().isEmpty() ||
                foreignKey.getTargetColumnNames() == null ||
                foreignKey.getTargetColumnNames().isEmpty()) {
            logMsg("invalid source/target columns. FK = " + foreignKey);
            invalid = true;
        }

        if(foreignKey.getSourceColumnNames().size() != foreignKey.getTargetColumnNames().size()) {
            logMsg("invalid source/target columns size. FK = " + foreignKey);
            invalid = true;
        }

        return !invalid;
    }

    private void writeRelationShip(OutputStreamWriter writer, Table table) throws IOException {
        for(ForeignKey foreignKey : table.getForeignKeyList()) {

            if(!validateForeignKey(foreignKey)) {
                continue;
            }

            writer.write(foreignKey.getSourceTableName());


            writeRelationshipFieldNames(writer, foreignKey.getSourceColumnNames());


            if(foreignKey.getJoinType().equals(ForeignKey.JOIN_TYPE_MANY_TO_ONE)) {
                //many to one.
                writer.write("}o--||");
            } else {
                //this is one to one.
                writer.write("|o--o|");
            }

            writeRelationshipFieldNames(writer, foreignKey.getTargetColumnNames());

            writer.write(foreignKey.getTargetTableName());

            writer.write(DocletUtil.ENTER);
            writer.write(DocletUtil.ENTER);
        }
    }

    private void writeRelationshipFieldNames(OutputStreamWriter writer, List<String> fieldNames) throws IOException{
        writer.write(" \"");
        boolean first = true;
        for(String sourceColumnName: fieldNames) {
            if(!first) {
                writer.write(",");
            }
            writer.write(sourceColumnName);
            first = false;
        }
        writer.write("\" ");
    }

    private void writeFooter(OutputStreamWriter writer) throws IOException  {

        writer.write("@enduml");

    }
}
