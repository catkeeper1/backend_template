package org.ckr.msdemo.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplictionExceptionHelper {

    private List<ExceptionMessage> exceptionMessages;

    private ApplictionExceptionHelper() {
        super();
    }

    public static ApplictionExceptionHelper createExpHelper() {
        return new ApplictionExceptionHelper();
    }

    /**
     * Add one more user message to this exception.
     * @param msgCode The message code which is used to retrive message from messageSource for i18n. It is also can
     *                be used to indicate the error.
     * @param params  The values for parameters inside the message template.
     */
    public ApplictionExceptionHelper addExceptionMessage( String msgCode, Serializable... params){
        if(exceptionMessages == null) {
            exceptionMessages = new ArrayList<>();
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        ExceptionMessage expMsg = new ExceptionMessage(msgCode, params,stackTrace);
        exceptionMessages.add(expMsg);

        return this;
    }

    /**
     * Throw this exception if exceptionMessage an non empty list.
     * This method is used for below scenario:
     * <ul>
     *     <li>There are multple validation rules.
     *     <li>For each rule, program will do one checking. If the checking failed, addMessage() will be called to
     *     add one message to the message list.
     *     <li>Call this method. If the message list is empty, that means all validations passed. If no, this
     *     exception will be thrown and all error messages will be shown to user.
     * </ul>
     */
    public void throwThisIfValid(String exceptionMessage) {
        if(exceptionMessages.isEmpty()) return;
        throw new ApplicationException(exceptionMessage, exceptionMessages);
    }
}
