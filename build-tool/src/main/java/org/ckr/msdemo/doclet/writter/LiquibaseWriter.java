package org.ckr.msdemo.doclet.writter;

import org.ckr.msdemo.doclet.model.Column;
import org.ckr.msdemo.doclet.model.DataModel;
import org.ckr.msdemo.doclet.model.Index;
import org.ckr.msdemo.doclet.model.Table;
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static org.ckr.msdemo.doclet.util.DocletUtil.*;

/**
 * Created by Administrator on 2017/6/20.
 */
public class LiquibaseWriter {

    private File baseDir;

    private DataModel dataModel;

    public LiquibaseWriter(String baseDirPath, DataModel dataModel) {
        createBaseDir(baseDirPath);
        this.dataModel = dataModel;
    }

    protected void createBaseDir(String baseDirPath) {
        File dir = new File(baseDirPath);

        if (!dir.isDirectory()) {
            throw new RuntimeException(dir.getAbsolutePath() + " is not a valid dir.");
        }

        dir = new File(dir, "liquibaseXml");

        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("cannot create directory:" + dir.getAbsolutePath());
            }
        }

        this.baseDir = dir;
    }

    private File createDocFile(Table table, String fileName) {


        File result = DocletUtil.createDirectory(this.baseDir,
            table.getPackageName().replace(".", "/"));


        result = new File(result, fileName);

        if (!result.exists()) {
            try {
                result.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("create not create new file " + result.getAbsolutePath(), ex);
            }
        }
        return result;
    }

    /**
     * Generate Ddl statement of Liquibase for tables.
     * <pre>
     *     <code>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <databaseChangeLog
     *         xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
     *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *         xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
     *          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
     *     <changeSet author="liquibase-docs" id="createTable-org.ckr.msdemo.adminservice.entity.MENU">
     *
     *         <createTable tableName="MENU">
     *             <column name="CODE" type="java.sql.Types.VARCHAR(100)">
     *                 <constraints nullable="false"/>
     *             </column>
     *             <column name="PARENT_CODE" type="java.sql.Types.VARCHAR(100)"/>
     *             <column name="DESCRIPTION" type="java.sql.Types.VARCHAR(200)"/>
     *             <column name="FUNCTION_POINT" type="java.sql.Types.VARCHAR(100)"/>
     *             <column name="MODULE" type="java.sql.Types.VARCHAR(100)"/>
     *         </createTable>
     *     </changeSet>
     *     <changeSet author="liquibase-docs" id="createTablePk-org.ckr.msdemo.adminservice.entity.MENU">
     *
     *         <addPrimaryKey constraintName="PK_MENU" columnNames="CODE" tableName="MENU" />
     *     </changeSet>
     * </databaseChangeLog>
     *     </code>
     * </pre>
     */
    public void generateDdlXmlConfigDoc() {
        for (Table table : dataModel.getTableList()) {

            File docFile = createDocFile(table, "db.changelog.create_" + table.getTableName() + ".xml");

            final LiquibaseWriter t = this;

            new FileWritterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeDdlDoc(table, writer);
                }
            }.execute();

        }

    }

    private void writeDdlDoc(Table table, OutputStreamWriter writter) throws IOException {
        //write header
        writter.write(DocletUtil.DOC_HEADER);

        writeTableContent(table, writter);

        writePrimaryContent(table, writter);

        writeIndexContent(table, writter);
        //the end
        writter.write(DocletUtil.DOC_END);
    }

    private void writeTableContent(Table table, OutputStreamWriter writter) throws IOException {
        writeChangeSet(writter, "createTable-" + table.getPackageName() + "." + table.getTableName());
        writter.write(ENTER);

        writter.write(indent(2) + "<createTable tableName=\""
            + table.getTableName() + "\">" + ENTER);

        for (Column column : table.getColumnList()) {
            this.writeColumnContent(column, writter);
        }

        writter.write(indent(2) + "</createTable>" + ENTER);
        writter.write(DocletUtil.CHANGE_SET_END);
    }

    private void writePrimaryContent(Table table, OutputStreamWriter writter) throws IOException {
        StringBuilder fieldNames = new StringBuilder("");
        for (Column column : table.getColumnList()) {
            if (Boolean.TRUE.equals(column.getIsPrimaryKey())) {

                if (fieldNames.length() > 0) {
                    fieldNames.append(",");
                }

                fieldNames.append(column.getName());

            }
        }

        if (fieldNames.length() == 0) {
            return;
        }

        writeChangeSet(writter, "createTablePk-" + table.getPackageName() + "." + table.getTableName());
        writter.write(ENTER);

        writter.write(indent(2) + "<addPrimaryKey "
            + "constraintName=\"" + "PK_" + table.getTableName() + "\" "
            + "columnNames=\"" + fieldNames.toString() + "\" "
            + "tableName=\"" + table.getTableName() + "\" />" + ENTER);

        writter.write(DocletUtil.CHANGE_SET_END);
    }

    private void writeIndexContent(Table table, OutputStreamWriter writter) throws IOException {

        if (table.getIndexList().isEmpty()) {
            return;
        }

        writeChangeSet(writter, "createTableIndex-" + table.getPackageName() + "." + table.getTableName());

        int noOfIndex = 0;

        for (Index index : table.getIndexList()) {

            boolean unique = false;
            if (Boolean.TRUE.equals(index.getUnique())) {
                unique = true;
            }

            String indexName = "IND_" + table.getTableName() + "_" + noOfIndex;

            if (index.getName() != null) {
                indexName = index.getName();
            }

            writter.write(indent(2) + "<createIndex "
                + "indexName=\"" + indexName + "\" "
                + "tableName=\"" + table.getTableName() + "\" "
                + "unique=\"" + unique + "\">" + ENTER);

            for (Index.IndexColumn indexColumn : index.getColumnList()) {

                writter.write(indent(3) + "<column name=\"" + indexColumn.getName() + "\"/>" + ENTER);

            }

            writter.write(indent(2) + "</createIndex>" + ENTER);

            noOfIndex++;
        }
        writter.write(DocletUtil.CHANGE_SET_END);
    }

    private void writeColumnContent(Column column, OutputStreamWriter writter) throws IOException {

        if (Boolean.TRUE.equals(column.getIsPrimaryKey())) {

            writter.write(indent(3)
                + "<column name=\"" + column.getName() + "\" type=\"" + getColumnType(column) + "\">" + ENTER);

            writter.write(indent(4) + "<constraints nullable=\"false\"/>" + ENTER);

            writter.write(indent(3)
                + "</column>" + ENTER);

        } else {
            writter.write(indent(3)
                + "<column name=\"" + column.getName() + "\" type=\"" + getColumnType(column) + "\"/>" + ENTER);
        }

    }

    private String getPrimaryKeyAttribute(Column column) {
        if (Boolean.TRUE.equals(column.getIsPrimaryKey())) {
            return "primaryKey=true";
        }

        return "";
    }

    /**
     * Generate insert statement of Liquibase for tables.
     * <pre>
     *     <code>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <databaseChangeLog
     *         xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
     *         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *         xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
     *          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
     *     <changeSet author="liquibase-docs" id="insertTable-org.ckr.msdemo.adminservice.entity.MENU" context="!UT">
     *
     *         <loadUpdateData file="org/ckr/msdemo/adminservice/entity/MENU.csv"
     *                         primaryKey="CODE"
     *                         tableName="MENU">
     *         </loadUpdateData>
     *     </changeSet>
     * </databaseChangeLog>
     *     </code>
     * </pre>
     */
    public void generateInsertXmlConfigDoc() {
        for (Table table : dataModel.getTableList()) {

            File docFile = createDocFile(table, "db.changelog.insert_" + table.getTableName() + ".xml");

            final LiquibaseWriter t = this;

            new FileWritterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeInsertDoc(table, writer);
                }
            }.execute();

        }

    }

    private void writeInsertDoc(Table table, OutputStreamWriter writter) throws IOException {
        //write header
        writter.write(DocletUtil.DOC_HEADER);

        writeInsertTableContent(table, writter);

        //the end
        writter.write(DocletUtil.DOC_END);
    }

    private void writeInsertTableContent(Table table, OutputStreamWriter writter) throws IOException {

        StringBuilder pkStr = new StringBuilder();

        for (Column column : table.getColumnList()) {
            if (Boolean.TRUE.equals(column.getIsPrimaryKey())) {

                if (pkStr.length() > 0) {
                    pkStr.append(",");
                }

                pkStr.append(column.getName());
            }
        }

        writeChangeSet(writter,
            "insertTable-"
                + table.getPackageName()
                + "." + table.getTableName(),
            "!UT");


        writter.write(ENTER);

        writter.write(indent(2) + "<loadUpdateData file=\""
            + table.getPackageName().replace('.', '/') + "/"
            + table.getTableName() + ".csv" + "\"" + ENTER);

        if (pkStr.length() > 0) {
            writter.write(indent(2) + "                primaryKey=\""
                + pkStr.toString() + "\"" + ENTER);
        }

        writter.write(indent(2) + "                tableName=\""
            + table.getTableName() + "\">" + ENTER);


        for (Column column : table.getColumnList()) {
            this.writeInsertColumnContent(column, writter);
        }

        writter.write(indent(2) + "</loadUpdateData>" + ENTER);
        writter.write(DocletUtil.CHANGE_SET_END);
    }

    private void writeInsertColumnContent(Column column, OutputStreamWriter writter) throws IOException {

        Class javaFileType = String.class;

        try {
            javaFileType = this.getClass().getClassLoader().loadClass(column.getJavaFieldType());
        } catch (ClassNotFoundException ex) {
            logMsg("Cannot load class:" + column.getJavaFieldType());
        }

        String columnType = null;

        if (Date.class.isAssignableFrom(javaFileType)) {
            columnType = "DATE";
        } else if (Number.class.isAssignableFrom(javaFileType)) {
            columnType = "NUMERIC";
        } else if (Boolean.class.isAssignableFrom(javaFileType)) {
            columnType = "BOOLEAN";
        }

        if (columnType == null) {
            return;
        }

        writter.write(indent(3)
            + "<column name=\"" + column.getName() + "\" type=\"" + columnType + "\"/>" + ENTER);

    }


    /**
     * Generate insert statement of Liquibase for tables
     * <pre><include file="org/ckr/msdemo/adminservice/entity/db.changelog.create_USER.xml"/></pre>
     */
    public void generateInsertCsvTemplate() {
        for (Table table : dataModel.getTableList()) {

            File docFile = createDocFile(table, table.getTableName() + ".csv");

            final LiquibaseWriter t = this;

            new FileWritterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeCsvTemplateHeader(table, writer);
                }
            }.execute();

        }

    }

    private void writeCsvTemplateHeader(Table table, OutputStreamWriter writer) throws IOException {

        for (int i = 0; i < table.getColumnList().size(); i++) {
            Column column = table.getColumnList().get(i);

            if (i > 0) {
                writer.write(",");
            }
            writer.write(column.getName());

        }


    }

    /**
     * Generate include statement of Liquibase for tables
     * <pre><include file="org/ckr/msdemo/adminservice/entity/db.changelog.create_USER.xml"/></pre>
     */
    public void generateIncludeXmlConfig() {
        for (Table table : dataModel.getTableList()) {

            File docFile = createDocFile(table, "db.changelog." + table.getTableName() + ".xml");

            final LiquibaseWriter t = this;

            new FileWritterTemplate(docFile) {

                @Override
                protected void doWrite(OutputStreamWriter writer) throws IOException {
                    t.writeIncludeContent(table, writer);
                }
            }.execute();

        }

    }

    private void writeIncludeContent(Table table, OutputStreamWriter writter) throws IOException {

        writter.write(DocletUtil.DOC_HEADER);

        writter.write(indent(1) + "<include file=\""
            + table.getPackageName().replace(".", "/") + "/"
            + "db.changelog.create_" + table.getTableName() + ".xml\"/>" + ENTER);

        writter.write(indent(1) + "<include file=\""
            + table.getPackageName().replace(".", "/") + "/"
            + "db.changelog.insert_" + table.getTableName() + ".xml\"/>" + ENTER);

        writter.write(DocletUtil.DOC_END);


    }

}
