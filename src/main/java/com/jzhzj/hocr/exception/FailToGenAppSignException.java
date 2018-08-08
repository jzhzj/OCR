package com.jzhzj.hocr.exception;

/**
 * Thrown to indicate that failed to generate the app sign.
 * Either no keys could be found in the config file or keys were filled in incorrectly.
 *
 * @author jzhzj
 */
public class FailToGenAppSignException extends Exception {
    /**
     * Constructs a <code>FailToGenAppSignException</code> with
     * <code>null</code> as its error detail message.
     */
    public FailToGenAppSignException() {
        super();
    }

    /**
     * Constructs a <code>FailToGenAppSignException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public FailToGenAppSignException(String message) {
        super(message);
    }
}
