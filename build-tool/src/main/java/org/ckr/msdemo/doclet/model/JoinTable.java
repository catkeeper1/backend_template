package org.ckr.msdemo.doclet.model;

import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import org.ckr.msdemo.doclet.util.AnnotationScanTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.ckr.msdemo.doclet.util.DocletUtil.getParameterTypeName;

/**
 * Created by Administrator on 2017/7/2.
 */
public class JoinTable {

    public static final String JOINTABLE_QUALIFIED_NAME = "javax.persistence.JoinTable";

    public static final String JOINTABLE_NAME = "name";

    public static final String JOINTABLE_JOINCOLUMNS = "joinColumns";

    public static final String JOINTABLE_INVERSECOLUMNS = "inverseJoinColumns";

    private String tableName = null;

    private String joinFullClassName = null;

    private String inverseFullClassName = null;

    private List<Index> indexList = new ArrayList<>();

    private List<Column> joinColumnList = new ArrayList<>();

    private List<Column> inverseColumnList = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    private void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJoinFullClassName() {
        return joinFullClassName;
    }

    private void setJoinFullClassName(String joinFullClassName) {
        this.joinFullClassName = joinFullClassName;
    }

    public String getInverseFullClassName() {
        return inverseFullClassName;
    }

    private void setInverseFullClassName(String inverseFullClassName) {
        this.inverseFullClassName = inverseFullClassName;
    }

    public List<Index> getIndexList() {
        return indexList;
    }

    private void setIndexList(List<Index> indexList) {
        this.indexList = indexList;
    }

    public List<Column> getJoinColumnList() {
        return joinColumnList;
    }

    private void setJoinColumnList(List<Column> joinColumnList) {
        this.joinColumnList = joinColumnList;
    }

    public List<Column> getInverseColumnList() {
        return inverseColumnList;
    }

    private void setInverseColumnList(List<Column> inverseColumnList) {
        this.inverseColumnList = inverseColumnList;
    }

    public static List<JoinTable> createJoinTable(ClassDoc classDoc, List<Table> existTableList) {

        List<JoinTable> result = new ArrayList<>();

        if (classDoc == null || existTableList == null || existTableList.isEmpty()) {
            return result;
        }


        final Set<String> includeTableName = new HashSet<>();

        new AnnotationScanTemplate<Set<String>>(classDoc, includeTableName)
            .annotation(Table.TABLE_QUALIFIED_NAME)
            .attribute(Table.TABLE_NAME, (data, annotationValue) -> data.add((String) annotationValue.value()))
            .parent()
            .scanProgramElement();

        if (includeTableName.isEmpty()) {
            return result;
        }

        MethodDoc[] methods = classDoc.methods();

        for (MethodDoc method : methods) {

            JoinTable instance = new JoinTable();

            new AnnotationScanTemplate<JoinTable>(method, instance)
                .annotation(JOINTABLE_QUALIFIED_NAME)
                .attribute(JOINTABLE_NAME,
                    (data, annotationValue) ->
                        data.setTableName((String) annotationValue.value()))
                .attribute(JOINTABLE_JOINCOLUMNS,
                    (data, annotationValue) ->
                        createJoinColumnList(data, (AnnotationValue[]) annotationValue.value()))
                .attribute(JOINTABLE_INVERSECOLUMNS,
                    (data, annotationValue) ->
                        createInverseColumnList(data, (AnnotationValue[]) annotationValue.value()))
                .parent()
                .scanProgramElement();

            if (instance.getTableName() == null) {
                continue;
            }

            instance.setJoinFullClassName(classDoc.qualifiedName());

            String inverseType = null;
            if (method.name().startsWith("get")) {

                inverseType = getParameterTypeName(method.returnType());

            } else if (method.name().startsWith("set")) {

                if (method.parameters() != null && method.parameters().length > 0) {
                    inverseType = getParameterTypeName(method.parameters()[0].type());
                }

            }

            if (inverseType == null) {
                throw new RuntimeException("cannot get join class type. method is "
                    + method.qualifiedName()
                    + ".class name is " + classDoc.qualifiedName());
            }

            instance.setInverseFullClassName(inverseType);

            result.add(instance);
        }
        return result;
    }



    private static void createJoinColumnList(JoinTable dataObject, AnnotationValue[] columnAnnotationList) {

        if (columnAnnotationList == null) {
            return;
        }

        for (AnnotationValue columnAnnotation : columnAnnotationList) {
            Column column = Column.createJoinTableColumn(columnAnnotation);

            if (column != null) {
                dataObject.joinColumnList.add(column);
            }
        }

    }

    private static void createInverseColumnList(JoinTable dataObject, AnnotationValue[] columnAnnotationList) {

        if (columnAnnotationList == null) {
            return;
        }

        for (AnnotationValue columnAnnotation : columnAnnotationList) {
            Column column = Column.createJoinTableColumn(columnAnnotation);

            if (column != null) {
                dataObject.inverseColumnList.add(column);
            }
        }

    }

    @Override
    public String toString() {
        return "JoinTable{"
            + "tableName='" + tableName + '\''
            + ", joinFullClassName='" + joinFullClassName + '\''
            + ", inverseFullClassName='" + inverseFullClassName + '\''
            + ", indexList=" + indexList
            + ", joinColumnList=" + joinColumnList
            + ", inverseColumnList=" + inverseColumnList
            + '}';
    }
}
