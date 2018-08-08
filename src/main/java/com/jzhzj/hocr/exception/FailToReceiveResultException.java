package com.jzhzj.hocr.exception;

/**
 * Thrown to indicate that failed to receive results from the cloud.
 *
 * @author jzhzj
 */
public class FailToReceiveResultException extends Exception {
    /**
     * Constructs a <code>FailToReceiveResultException</code> with
     * <code>null</code> as its error detail message.
     */
    public FailToReceiveResultException() {
        super();
    }

    /**
     * Constructs a <code>FailToReceiveResultException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public FailToReceiveResultException(String message) {
        super(message);
    }
}
