package org.ckr.msdemo.exception;

/**
 * Created by Administrator on 2017/8/12.
 */
public class ReThrownApplicationException extends ApplicationException {

    private static final long serialVersionUID = -389413773965151203L;

    public ReThrownApplicationException(String shortDescription, String exceptionId) {
        super(shortDescription, false);
        this.exceptionId = exceptionId;
    }

//    /**
//     * Add message with message code and message.
//     *
//     * @param msgCode message code
//     * @param message message
//     * @return ReThrownApplicationException
//     */
//    public ReThrownApplicationException addMessage(String msgCode, String message) {
//        super.addMessage(msgCode, message, null);
//        return this;
//    }
}
