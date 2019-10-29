package org.ckr.msdemo.doclet.exception;

public class DocletException extends RuntimeException {
    public DocletException() {
    }

    public DocletException(String message) {
        super(message);
    }

    public DocletException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocletException(Throwable cause) {
        super(cause);
    }

    public DocletException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
