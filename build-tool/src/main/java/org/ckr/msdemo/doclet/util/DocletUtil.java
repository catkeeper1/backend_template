package org.ckr.msdemo.doclet.util;

import com.sun.javadoc.*;
import org.ckr.msdemo.doclet.model.Column;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility that contains meta statements and useful methods.
 */
public class DocletUtil {

    public static final String ENTER = "\r\n";

    public static final String DOC_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ENTER
            + "<databaseChangeLog" + ENTER
            + "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"" + ENTER
            + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + ENTER
            + "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog" + ENTER
            + "         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\">" + ENTER;

    public static final String DOC_END = "</databaseChangeLog>";

    public static void writeChangeSet(OutputStreamWriter writter, String changeId) throws IOException {
        writter.write(indent(1) + "<changeSet author=\"liquibase-docs\" id=\""
            + changeId + "\">" + ENTER);
    }

    public static void writeChangeSet(OutputStreamWriter writter, String changeId, String context) throws IOException {
        writter.write(indent(1) + "<changeSet author=\"liquibase-docs\" id=\""
            + changeId + "\" context=\"" + context + "\">" + ENTER);
    }

    public static final String CHANGE_SET_END = indent(1) + "</changeSet>" + ENTER;

    public static void logMsg(String msg) {
        //can be replaced by other logger later.
        System.out.println(msg);
    }

    /**
     * Find annotation with qualified name.
     *
     * @param classDoc      classDoc
     * @param qualifiedName qualifiedName
     * @return AnnotationDesc
     */
    public static AnnotationDesc findAnnotation(ProgramElementDoc classDoc, String qualifiedName) {

        AnnotationDesc[] anntations = classDoc.annotations();

        if (anntations == null) {
            return null;
        }

        List<AnnotationDesc> result = new ArrayList<AnnotationDesc>();

        for (AnnotationDesc annotation : anntations) {
            if (qualifiedName.equals(annotation.annotationType().qualifiedName())) {
                return annotation;
            }
        }

        return null;
    }

    /**
     * Find the annotationValue of a specific key in a annotation.
     *
     * @param annotation    annotation
     * @param qualifiedName key
     * @return AnnotationValue
     */
    public static AnnotationValue getAnnotationAttribute(AnnotationDesc annotation, String qualifiedName) {

        if (annotation.elementValues() == null) {
            return null;
        }

        for (AnnotationDesc.ElementValuePair pair : annotation.elementValues()) {

            if (qualifiedName.equals(pair.element().qualifiedName())) {
                return pair.value();
            }

        }

        return null;
    }

    /**
     * Find the string value of a specific key in a annotation.
     *
     * @param annotation    annotation
     * @param qualifiedName key
     * @return string value
     */
    public static String getAnnotationAttributeStringValue(AnnotationDesc annotation, String qualifiedName) {

        if (annotation.elementValues() == null) {
            return null;
        }
        for (AnnotationDesc.ElementValuePair pair : annotation.elementValues()) {

            if (qualifiedName.equals(pair.element().qualifiedName())) {
                return (String) pair.value().value();
            }
        }
        return null;
    }

    /**
     * Find Methods with specific qualified name in specific class.
     *
     * @param classDoc      classDoc
     * @param qualifiedName qualifiedName
     * @return List of MethodDoc
     */
    public static List<MethodDoc> findMethodWithAnnotation(ClassDoc classDoc, String qualifiedName) {

        List<MethodDoc> result = new ArrayList<MethodDoc>();

        if (classDoc.methods() == null) {
            return result;
        }


        for (MethodDoc methodDoc : classDoc.methods()) {
            AnnotationDesc columnAnnotation =
                findAnnotation(methodDoc, qualifiedName);

            if (columnAnnotation == null) {
                continue;
            }

            result.add(methodDoc);
        }

        return result;

    }

    /**
     * Get class name from the full qualified name of class.
     *
     * @param classDoc classDoc
     * @return class name
     */
    public static String getPackageName(ClassDoc classDoc) {

        if (!classDoc.qualifiedTypeName().contains(".")) {
            return "";
        }

        return classDoc.qualifiedTypeName().substring(0, classDoc.qualifiedTypeName().lastIndexOf("."));

    }

    /**
     * Get field name from the getter/setter method name.
     *
     * @param method MethodDoc
     * @return field name
     */
    public static String getMethodName(MethodDoc method) {
        String name = method.name();

        if (name.length() <= 3) {
            return name;
        }

        name = name.substring(3, 4).toLowerCase() + name.substring(4, name.length());

        return name;

    }

    public static String getFieldTypeName(MethodDoc method) {
        return method.returnType().qualifiedTypeName();

    }

    public static String getOutputDirPath() {
        return System.getProperty("output");
    }

    /**
     * Create Directories at baseDir followed by the hierarchy of path separated by period.
     *
     * @param baseDir baseDir
     * @param path    path
     * @return baseDir
     */
    public static File createDirectory(File baseDir, String path) {

        File result = baseDir;

        StringTokenizer tokenizer = new StringTokenizer(path, ".");

        while (tokenizer.hasMoreTokens()) {
            result = new File(result, tokenizer.nextToken());
            if (!result.exists()) {
                if (!result.mkdirs()) {
                    throw new RuntimeException("Cannot create directory " + result.getAbsolutePath());
                }
            }
        }

        return result;

    }

    /**
     * Get a string of blank spaces specify by noOfIndent.
     *
     * @param noOfIndent noOfIndent
     * @return string of blank spaces
     */
    public static String indent(int noOfIndent) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < noOfIndent; i++) {
            result.append("    ");
        }
        return result.toString();
    }

    public static String getColumnTypeForErDiagram(Column column) {
        String result = getColumnType(column);

        if(result.startsWith("java.sql.Types.")) {
            result = result.substring("java.sql.Types.".length());
        }

        return result;
    }

    /**
     * Get fully qualified name of the Column type.
     *
     * @param column Column
     * @return ColumnType
     */
    public static String getColumnType(Column column) {
        String result = "";

        Integer length = null;
        Integer scale = null;
        Integer precision = null;

        if (String.class.getName().equals(column.getJavaFieldType())) {
            length = 100;
            result = "java.sql.Types.VARCHAR";
        } else if (Boolean.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.BOOLEAN";
        } else if (Date.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.DATE";
        } else if (java.sql.Date.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.DATE";
        } else if (Timestamp.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.TIMESTAMP";
        } else if (Long.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.BIGINT";
        } else if (Integer.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.INTEGER";
        } else if (Short.class.getName().equals(column.getJavaFieldType())) {
            result = "java.sql.Types.SMALLINT";
        } else if (BigDecimal.class.getName().equals(column.getJavaFieldType())) {
            scale = 19;
            precision = 4;
            result = "java.sql.Types.DECIMAL";
        }

        if (column.getColumnDefinition() != null && column.getColumnDefinition().trim().length() > 0) {
            result = column.getColumnDefinition();
        }

        if (column.getLength() != null) {
            length = column.getLength();
        }

        if (column.getScale() != null) {
            scale = column.getScale();
        }

        if (column.getPrecision() != null) {
            precision = column.getPrecision();
        }

        if (length != null) {
            result = result + "(" + length + ")";
        } else if (scale != null) {
            result = result + "(" + scale + ", " + precision + ")";
        }

        return result;
    }

    public static String getParameterTypeName(Type type) {

        ClassDoc classDoc = getParameterTypeClassDoc(type);

        if(classDoc == null) {
            return null;
        }

        return classDoc.qualifiedName();

    }

    public static ClassDoc getParameterTypeClassDoc(Type type) {

        if (type == null) {
            return null;
        }

        Type[] typeParams = type.asParameterizedType().typeArguments();

        if (typeParams == null || typeParams.length == 0) {
            return null;
        }

        return typeParams[0].asClassDoc();

    }

}
