package org.ckr.msdemo.doclet.util;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ProgramElementDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */
public class AnnotationScanTemplate<T> {

    private ProgramElementDoc programElementDoc;

    private T dataObject;

    private List<BasicAnnotationHandler<T>> annotationHandlerList = new ArrayList();


    public AnnotationScanTemplate(ProgramElementDoc programElementDoc, T dataObject) {
        this.programElementDoc = programElementDoc;
        this.dataObject = dataObject;
    }

    private AnnotationScanTemplate<T> addAnnotationHandler(BasicAnnotationHandler<T> handler) {

        handler.setParent(this);
        annotationHandlerList.add(handler);
        return this;
    }

    public AnnotationHandler<T> annotation(String qualifeidName) {
        BasicAnnotationHandler<T> handler = new BasicAnnotationHandler<T>(qualifeidName);
        addAnnotationHandler(handler);
        return handler;
    }

    public AnnotationHandler<T> annotation(String qualifeidName, SetDataWithAnnotationFunction<T> fun) {
        BasicAnnotationHandler<T> handler = new BasicAnnotationHandler<T>(qualifeidName, fun);
        addAnnotationHandler(handler);
        return handler;
    }

    public void scanProgramElement() {

        AnnotationDesc[] anntations = programElementDoc.annotations();

        if (anntations == null) {
            return;
        }

        List<AnnotationDesc> result = new ArrayList<AnnotationDesc>();

        for (AnnotationDesc annotation : anntations) {

            callAnnotationHandlers(annotation);

        }


    }

    public static <D> D scanAnnotation(D dataObject, AnnotationValue annotation) {

        System.out.println(annotation.value().getClass().toString());

        AnnotationValue[] value = (AnnotationValue[]) annotation.value();

        for (AnnotationValue v : value) {
            System.out.println(v);
        }


        return dataObject;
    }

    private void callAnnotationHandlers(AnnotationDesc annotation) {
        for (BasicAnnotationHandler<T> handler : this.annotationHandlerList) {
            if (!handler.supported(annotation)) {
                continue;
            }
            handler.handle(this.dataObject, annotation);
        }

    }


    public interface AnnotationHandler<T> {

        AnnotationScanTemplate<T> parent();

        AnnotationHandler<T> attribute(String attributeName, SetDataWithAttributeFunction<T> setDataFunction);

        void handle(T dataObject, AnnotationDesc annotation);
    }

    public static class BasicAnnotationHandler<T> implements AnnotationHandler<T> {

        private String annotationQualifiedName;

        private List<BasicAnnotationAttributaHandler<T>> annotationAttributeHandlerList = new ArrayList();

        private AnnotationScanTemplate parent;

        private SetDataWithAnnotationFunction<T> setDataWithAnnotationFunction = null;

        public BasicAnnotationHandler(String annotationQualifiedName) {
            this.annotationQualifiedName = annotationQualifiedName;
        }

        public BasicAnnotationHandler(String annotationQualifiedName,
                                      SetDataWithAnnotationFunction<T> setDataWithAnnotationFunction) {
            this.annotationQualifiedName = annotationQualifiedName;
            this.setDataWithAnnotationFunction = setDataWithAnnotationFunction;
        }

        public BasicAnnotationHandler() {
            this.annotationQualifiedName = "";
        }

        public BasicAnnotationHandler<T> addAnnotationAttributeHandler(BasicAnnotationAttributaHandler<T> handler) {

            handler.setParent(this);
            annotationAttributeHandlerList.add(handler);
            return this;
        }


        public boolean supported(AnnotationDesc annotation) {
            return this.annotationQualifiedName.equals(annotation.annotationType().qualifiedName());
        }

        @Override
        public void handle(T dataObject, AnnotationDesc annotation) {

            preHandle(dataObject, annotation);

            if (annotation.elementValues() == null) {
                return;
            }

            for (AnnotationDesc.ElementValuePair valuePair : annotation.elementValues()) {

                callAnnotationAttributeHandlers(dataObject, annotation, valuePair);

            }

        }


        public void setParent(AnnotationScanTemplate parent) {
            this.parent = parent;
        }

        @Override
        public AnnotationScanTemplate parent() {
            return this.parent;
        }

        @Override
        public AnnotationHandler<T> attribute(String attributeName, SetDataWithAttributeFunction<T> setDataFunction) {

            BasicAnnotationAttributaHandler<T> handler = new BasicAnnotationAttributaHandler<T>(attributeName,
                setDataFunction);
            addAnnotationAttributeHandler(handler);
            return this;

        }

        protected void preHandle(T dataObject, AnnotationDesc annotation) {
            if (this.setDataWithAnnotationFunction != null) {
                this.setDataWithAnnotationFunction.setData(dataObject, annotation);
            }
        }

        private void callAnnotationAttributeHandlers(T dataObject,
                                                     AnnotationDesc annotation,
                                                     AnnotationDesc.ElementValuePair valuePair) {
            for (BasicAnnotationAttributaHandler<T> handler : this.annotationAttributeHandlerList) {

                if (!handler.supported(annotation, valuePair)) {
                    continue;
                }

                handler.handle(dataObject, annotation, valuePair);
            }

        }
    }


    public static class BasicAnnotationAttributaHandler<T> {

        private String attributeName;

        private AnnotationHandler<T> parent;

        private SetDataWithAttributeFunction<T> setDataFunction;

        public BasicAnnotationAttributaHandler(String attributeName, SetDataWithAttributeFunction<T> setDataFunction) {

            this.attributeName = attributeName;
            this.setDataFunction = setDataFunction;
        }


        public boolean supported(AnnotationDesc annotation, AnnotationDesc.ElementValuePair valuePair) {
            return this.attributeName.equals(valuePair.element().name());
        }


        public void handle(T dataObject, AnnotationDesc annotation, AnnotationDesc.ElementValuePair valuePair) {
            setDataFunction.setData(dataObject, valuePair.value());
        }


        public void setParent(AnnotationHandler<T> parent) {
            this.parent = parent;
        }


        public AnnotationHandler<T> parent() {
            return this.parent;
        }

    }

    public interface SetDataWithAnnotationFunction<T> {
        void setData(T dataObject, AnnotationDesc annotation);
    }

    public interface SetDataWithAttributeFunction<T> {
        void setData(T dataObject, AnnotationValue annotationValue);
    }
}


