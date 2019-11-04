package org.ckr.msdemo.exception;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class ExceptionMessage implements Serializable {
    private static final long serialVersionUID = 887496710964984427L;

    private String messageCode = null;


    private Serializable[] messageParams = new Serializable[0];

    private String message = null;

    private StackTraceElement[] stacks;


    /**
     *
     * @param code The message code that will be used to retrieve user message from messageSource object.
     * @param params  The values of parameters of the message template.
     */
    public ExceptionMessage(String code, Serializable[] params , StackTraceElement[] stacks) {
        this.messageCode = code;
        if (params != null && params.length > 0) {
            this.messageParams = new Serializable[params.length];
            System.arraycopy(params, 0, this.messageParams, 0, params.length);
        }
        this.message = null;
        this.stacks = stacks;
    }

    /**
     *
     * @param code The code indicate the error
     * @param message The message that should be shown to end user. It is expected that the micro service that
     *                throw exception provide this message.
     */
    public ExceptionMessage(String code, String message ,StackTraceElement[] stacks) {
        this.messageCode = code;
        this.message = message;
        this.stacks = stacks;
    }

    /**
     * The get method for message code.
     * @return The message code that will be used to retrieve user message from messageSource object.
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * The get method for message params.
     * @return the values of parameters of message template.
     */
    public Object[] getMessageParams() {

        if (this.messageParams.length > 0) {
            int arrayLen = this.messageParams.length;

            Object[] result = new Object[arrayLen];
            Object[] srcParams = this.messageParams;

            System.arraycopy(srcParams, 0, result, 0, arrayLen);
            return result;
        }

        return new Object[0];
    }

    /**
     * Return he error message that will be shown to end user.
     * @return The error message that will be shown to end user.
     */
    public String getMessage() {
        return message;
    }

    public StackTraceElement[] getStacks() {
        return stacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExceptionMessage that = (ExceptionMessage) o;
        return Objects.equals(messageCode, that.messageCode) &&
                Arrays.equals(messageParams, that.messageParams) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageCode, messageParams, message);
    }

}
