package org.ckr.msdemo.exception;

/**
 * Created by Administrator on 2017/8/12.
 */
public class ReThrownSystemException extends SystemException {

    private static final long serialVersionUID = -4402478111395087780L;

    public ReThrownSystemException(String shortDescription, String exceptionId) {
        super(shortDescription, false);
        this.exceptionId = exceptionId;
    }
}
