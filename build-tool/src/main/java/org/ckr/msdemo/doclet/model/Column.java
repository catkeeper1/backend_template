package org.ckr.msdemo.doclet.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import org.ckr.msdemo.doclet.util.AnnotationScanTemplate;
import org.ckr.msdemo.doclet.util.DocletUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */
public class Column {

    public static final String COLUMN_QUALIFIED_NAME = "javax.persistence.Column";

    public static final String COLUMN_ID_QUALIFIED_NAME = "javax.persistence.Id";

    public static final String EMBEDDED_ID_QUALIFIED_NAME = "javax.persistence.EmbeddedId";

    public static final String COLUMN_NAME = "name";

    public static final String JOIN_TABLE_COLUMN_NAME = "name";

    public static final String COLUMN_NULLABLE = "nullable";

    public static final String COLUMN_DEFINITION = "columnDefinition";

    public static final String COLUMN_LENGTH = "length";

    public static final String COLUMN_PRECISION = "precision";

    public static final String COLUMN_SCALE = "scale";


    private String name = null;

    private Boolean nullable = true;

    private Boolean isPrimaryKey = false;

    private String columnDefinition;

    private Integer length;

    private Integer precision;

    private Integer scale;

    private String javaFieldName;

    private String javaFieldType;

    private String comment;

    public String getComment() {
        return comment;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    private void setJavaFieldName(String javaFieldName) {
        this.javaFieldName = javaFieldName;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Boolean getNullable() {
        return nullable;
    }

    private void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    private void setIsPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    private void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public Integer getLength() {
        return length;
    }

    private void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    private void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    private void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getJavaFieldType() {
        return javaFieldType;
    }

    private void setJavaFieldType(String javaFieldType) {
        this.javaFieldType = javaFieldType;
    }

    private Column() {

    }

    /**
     * Create columns for @EmbeddedId scenario.
     * @param method a java method java doc object.
     * @return  a list of Columns if the method has @EmbeddedId annotation.
     */
    private static List<Column> createEmbeddedIdColumns(MethodDoc method) {

        List<Column> result = new ArrayList<>();

        new AnnotationScanTemplate<List<Column>>(method, result)
            .annotation(EMBEDDED_ID_QUALIFIED_NAME, (resultList, annotationValue)
                    -> createEmbeddedColumn(method, resultList, true))
            .parent()
            .scanProgramElement();


        return result;

    }

    private static List<Column> createEmbeddedColumn(MethodDoc methodDoc, List<Column> resultList, boolean isPk) {
        ClassDoc embeddedClass = null;

        //assume the @EmbeddedId is placed at get method.
        if (methodDoc.name().startsWith("get")) {
            embeddedClass = methodDoc.returnType().asClassDoc();

            DocletUtil.logMsg("The embedded class is " + embeddedClass.qualifiedName());
        } else {
            DocletUtil.logMsg("Cannot find embedded class.");
            return resultList;
        }

        MethodDoc[] methods = embeddedClass.methods();

        if (methods == null) {
            return resultList;
        }

        for (MethodDoc method : methods) {

            Column column = createNormalColumn(method);

            if(column != null) {
                column.setIsPrimaryKey(isPk);
                resultList.add(column);
                continue;
            }


        }

        return resultList;
    }

    private static Column createNormalColumn(MethodDoc method) {
        Column column = new Column();


        new AnnotationScanTemplate<Column>(method, column)
                .annotation(COLUMN_QUALIFIED_NAME)
                .attribute(COLUMN_NAME, (data, annotationValue) -> data.setName((String) annotationValue.value()))
                .attribute(COLUMN_NULLABLE,
                        (data, annotationValue) -> data.setNullable((Boolean) annotationValue.value()))
                .attribute(COLUMN_DEFINITION,
                        (data, annotationValue) -> data.setColumnDefinition((String) annotationValue.value()))
                .attribute(COLUMN_LENGTH,
                        (data, annotationValue) -> data.setLength((Integer) annotationValue.value()))
                .attribute(COLUMN_PRECISION, (data, annotationValue) ->
                        data.setPrecision((Integer) annotationValue.value()))
                .attribute(COLUMN_SCALE, (data, annotationValue) ->
                        data.setScale((Integer) annotationValue.value()))
                .parent()
                .annotation(COLUMN_ID_QUALIFIED_NAME, (data, annotationValue) -> data.setIsPrimaryKey(true))
                .parent()
                .scanProgramElement();


        if (column.getName() == null) {
            return null;
        }

        column.setJavaFieldName(DocletUtil.getMethodName(method));
        column.setJavaFieldType(DocletUtil.getFieldTypeName(method));

        column.setComment(method.commentText());

        return column;
    }

    /**
     * Create document for all columns.
     *
     * @param classDoc classDoc
     * @return List of Column
     */
    public static List<Column> createColumns(ClassDoc classDoc) {

        List<Column> result = new ArrayList();

        MethodDoc[] methods = classDoc.methods();

        if (methods == null) {
            return result;
        }

        for (MethodDoc method : methods) {

            Column column = createNormalColumn(method);

            if(column != null) {
                result.add(column);
                continue;
            }

            List<Column> embeddedColumns = createEmbeddedIdColumns(method);

            result.addAll(embeddedColumns);
        }

        if (result.size() > 0 && classDoc.superclass() != null) {

            List<Column> superClassResult = createColumns(classDoc.superclass());

            result.addAll(superClassResult);
        }

        return result;
    }

    /**
     * Create document for all Join Table Column.
     *
     * @param indexAnnotation indexAnnotation
     * @return null
     */
    public static Column createJoinTableColumn(AnnotationValue indexAnnotation) {

        Column result = new Column();

        AnnotationScanTemplate.BasicAnnotationHandler<Column> annotationHandler =
            new AnnotationScanTemplate.BasicAnnotationHandler<>();


        annotationHandler
            .attribute(JOIN_TABLE_COLUMN_NAME,
                (data, annotationValue) -> data.setName((String) annotationValue.value()))
            .handle(result, (AnnotationDesc) indexAnnotation.value());

        if (result.getName() != null) {
            return result;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Column{"
            + "name='" + name + '\''
            + ", nullable=" + nullable
            + ", isPrimaryKey=" + isPrimaryKey
            + ", columnDefinition='" + columnDefinition + '\''
            + ", length=" + length
            + ", precision=" + precision
            + ", scale=" + scale
            + ", javaFieldName='" + javaFieldName + '\''
            + ", javaFieldType=" + javaFieldType
            + ", comment=" + comment
            + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Column column = (Column) object;

        if (name != null ? !name.equals(column.name) : column.name != null) {
            return false;
        }
        if (nullable != null ? !nullable.equals(column.nullable) : column.nullable != null) {
            return false;
        }
        if (isPrimaryKey != null ? !isPrimaryKey.equals(column.isPrimaryKey) : column.isPrimaryKey != null) {
            return false;
        }
        if (columnDefinition != null ? !columnDefinition.equals(column.columnDefinition)
            : column.columnDefinition != null) {
            return false;
        }
        if (length != null ? !length.equals(column.length) : column.length != null) {
            return false;
        }
        if (precision != null ? !precision.equals(column.precision) : column.precision != null) {
            return false;
        }
        if (scale != null ? !scale.equals(column.scale) : column.scale != null) {
            return false;
        }
        if (javaFieldName != null ? !javaFieldName.equals(column.javaFieldName) : column.javaFieldName != null) {
            return false;
        }
        if (javaFieldType != null ? !javaFieldType.equals(column.javaFieldType) : column.javaFieldType != null) {
            return false;
        }
        return comment != null ? comment.equals(column.comment) : column.comment == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (nullable != null ? nullable.hashCode() : 0);
        result = 31 * result + (isPrimaryKey != null ? isPrimaryKey.hashCode() : 0);
        result = 31 * result + (columnDefinition != null ? columnDefinition.hashCode() : 0);
        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (scale != null ? scale.hashCode() : 0);
        result = 31 * result + (javaFieldName != null ? javaFieldName.hashCode() : 0);
        result = 31 * result + (javaFieldType != null ? javaFieldType.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
