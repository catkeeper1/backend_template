package org.ckr.msdemo.exception.valueobject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.ckr.msdemo.exception.ApplicationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This is used by exception handler to define the json message format for responding when exception happen.
 * When an exception happen, exception handler will insert data into this object return it as response body.
 * Spring MVC framework will generate the json message for the exception base on the get methods inside this object.
 * For the json message format for exception handling, please refer all get methods in this class and
 * {@link ErrorMessage}.
 */
@ApiModel(value =  "ErrorResponse", description = "The data model for exception message")
public class ErrorResponse implements Serializable {


    private static final long serialVersionUID = 1687427709624458223L;

    private String exceptionId;
    private List<ErrorMessage> messageList;

    @ApiModelProperty(value = "For every instance of exception, a unique ID is generated. This ID is used to " +
    "trace detail error message in log.")
    public String getExceptionId() {
        return exceptionId;
    }


    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    /**
     * Return all user messages.
     * @return A list that include all user messages.
     */
    public List<ErrorMessage> getMessageList() {
        return messageList;
    }

    /**
     * Add a new user message to this object.
     * @param msgCode    The message code that can be use to retrieve message from messageSource objects.
     * @param msg        The user message.
     */
    public void addMessage(String msgCode, String msg) {
        if (this.messageList == null) {
            this.messageList = new ArrayList<ErrorMessage>();
        }

        ErrorMessage expMsg = new ErrorMessage(msgCode, msg);

        messageList.add(expMsg);

    }


    public Date getServerTime() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * This is an internal class used by {@link ErrorResponse} to store user message. One instance of this class
     * represent one user message. This class will be used to generate the json message for exception handling so that
     * it also impact the json message format in response.
     */
    public static class ErrorMessage implements Serializable {

        private static final long serialVersionUID = -8659613208164591992L;

        private String messageCode = null;
        private String message = null;

        public ErrorMessage() {
            super();
        }

        public ErrorMessage(String messageCode, String message) {
            this.message = message;
            this.messageCode = messageCode;
        }

        /**
         * @see ApplicationException.ExceptionMessage#getMessageCode()
         * Because this message code can be used by client side program to identify what kind of application exception
         *     is thrown so that it is expected to be included in the response as well.
         */
        public String getMessageCode() {
            return messageCode;
        }

        /**
         * @see ApplicationException.ExceptionMessage#getMessage()
         */
        public String getMessage() {
            return message;
        }


    }

}
