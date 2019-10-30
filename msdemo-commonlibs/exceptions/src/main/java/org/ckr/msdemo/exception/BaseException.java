package org.ckr.msdemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the basic exception for this system. All other customized exception type should
 * extend this class. <br>
 * Every instance of this class generate an exception ID will can be used to identify the stack message in log file.
 * For every instance of this class or its sub class, when the instance is log with any logger, the exception ID must
 * be included in the log message. Furthermore, exception handler should include the exception ID in the HTTP response.
 * It is expected that frontend program will use something like "console.log()" to log the exception ID or show it to
 * end user. So, when an exception happen, user can send this exception to help desk and IT support can use it to find
 * the stack info from log file easily. <br>
 * The format of exception ID is yyMMddkkmmssSSS(the machine time when this instance is created) + a four digit random
 * number. For example, if the machine time is "2003-Jul-02 10:32:24 345" the exception ID maybe 200307021032243451234.
 *
 */
public class BaseException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(BaseException.class);

    private static final long serialVersionUID = 9112831069901766558L;

    protected String exceptionId;


    protected BaseException() {
        super();
        genExceptionId();
    }

    protected BaseException(String shortDescription, boolean generateExpId) {
        super(shortDescription);
        if (generateExpId) {
            genExceptionId();
        }
    }

    protected BaseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        genExceptionId();
    }

    protected BaseException(String arg0) {
        super(arg0);
        genExceptionId();
    }

    protected BaseException(Throwable arg0) {
        super(arg0);
        genExceptionId();
    }

    private void genExceptionId() {
        if (exceptionId != null) {
            return;
        }

        exceptionId = generateExceptionID();
    }

    public static String generateExceptionID() {
        DateFormat format = new SimpleDateFormat("yyMMddkkmmssSSS");
        StringBuilder buffer = new StringBuilder();

        buffer.append(format.format(new Date()));

        NumberFormat numberFormat = new DecimalFormat("0000");
        numberFormat.setMaximumIntegerDigits(4);

        String randomStr = numberFormat.format(new Object().hashCode());
        buffer.append(randomStr);

        String exceptionId = buffer.toString();

        LOG.debug("generated exception ID is {}", exceptionId);

        return exceptionId;
    }

    public String getExceptionId() {
        return exceptionId;
    }


    //    @Override
    //    public void printStackTrace(PrintStream stream) {
    //        stream.append("Exception ID:" + getExceptionId());
    //        super.printStackTrace(stream);
    //    }
    //
    //    @Override
    //    public void printStackTrace(PrintWriter writer) {
    //        writer.append("Exception ID:" + getExceptionId());
    //        super.printStackTrace(writer);
    //    }

    /**
     * Override this method and include the exception ID in the message.
     * @return a message for this exception.
     */
    @Override
    public String getMessage() {
        StringBuilder result = new StringBuilder(50);

        result.append("Exception ID:")
              .append(getExceptionId())
              .append("\r\n")
              .append(super.getMessage());

        return result.toString();
    }
}
