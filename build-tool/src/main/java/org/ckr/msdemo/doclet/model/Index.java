package org.ckr.msdemo.doclet.model;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import org.ckr.msdemo.doclet.util.AnnotationScanTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2017/6/18.
 */
public class Index {


    public static final String INDEX_NAME = "name";
    public static final String INDEX_COLUMN_LIST = "columnList";
    public static final String INDEX_UNIQUE = "unique";

    private String name = null;

    private List<IndexColumn> columnList = new ArrayList<>();

    private Boolean unique = null;

    private Index() {
    }

    public static Index createIndex(AnnotationValue indexAnnotation) {

        Index result = new Index();

        AnnotationScanTemplate.BasicAnnotationHandler<Index> annotationHandler =
            new AnnotationScanTemplate.BasicAnnotationHandler<Index>();


        annotationHandler
            .attribute(INDEX_NAME,
                (data, annotationValue) -> data.setName((String) annotationValue.value()))
            .attribute(INDEX_COLUMN_LIST,
                (data, annotationValue) -> data.addColumn((String) annotationValue.value()))
            .attribute(INDEX_UNIQUE,
                (data, annotationValue) -> data.setUnique((Boolean) annotationValue.value()))
            .handle(result, (AnnotationDesc) indexAnnotation.value());

        if (result.getName() != null) {
            return result;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Boolean getUnique() {
        return unique;
    }

    private void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public List<IndexColumn> getColumnList() {
        return columnList;
    }

    private void addColumn(String columnListValue) {

        StringTokenizer columnListTokenizer = new StringTokenizer(columnListValue, ",");

        while (columnListTokenizer.hasMoreTokens()) {
            String columnValue = columnListTokenizer.nextToken();

            IndexColumn indexColumn = new IndexColumn();

            StringTokenizer columnTokenizer = new StringTokenizer(columnValue, " ");

            indexColumn.setName(columnTokenizer.nextToken());

            if (columnTokenizer.hasMoreTokens()) {
                indexColumn.setOrder(columnTokenizer.nextToken());
            }
            this.columnList.add(indexColumn);
        }


    }

    @Override
    public String toString() {
        return "Index{"
            + "name='" + name + '\''
            + ", columnList=" + columnList
            + ", unique=" + unique
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

        Index index = (Index) object;

        if (name != null ? !name.equals(index.name) : index.name != null) {
            return false;
        }
        if (columnList != null ? !columnList.equals(index.columnList) : index.columnList != null) {
            return false;
        }
        return unique != null ? unique.equals(index.unique) : index.unique == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (columnList != null ? columnList.hashCode() : 0);
        result = 31 * result + (unique != null ? unique.hashCode() : 0);
        return result;
    }

    public static class IndexColumn {

        private String name;

        private String order;

        public String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        public String getOrder() {
            return order;
        }

        private void setOrder(String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return "IndexColumn{"
                + "name='" + name + '\''
                + ", order='" + order + '\''
                + '}';
        }
    }
}
