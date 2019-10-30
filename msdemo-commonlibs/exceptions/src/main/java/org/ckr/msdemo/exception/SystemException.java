package org.ckr.msdemo.exception;


public class SystemException extends BaseException {

    private static final long serialVersionUID = 1087557111023898204L;

    protected SystemException() { 
        super();
    }

    protected SystemException(String shortDescription, boolean generateExpId) {
        super(shortDescription, generateExpId);
    }

    public SystemException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SystemException(String arg0) {
        super(arg0);
    }

    public SystemException(Throwable arg0) {
        super(arg0);
    }

}
