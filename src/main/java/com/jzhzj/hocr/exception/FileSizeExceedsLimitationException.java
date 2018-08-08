package com.jzhzj.hocr.exception;

/**
 * Thrown to indicate that the file chose by the user exceeds the size limitation.
 *
 * @author jzhzj
 */
public class FileSizeExceedsLimitationException extends Exception {
    /**
     * Constructs a <code>FileSizeExceedsLimitationException</code> with
     * <code>null</code> as its error detail message.
     */
    public FileSizeExceedsLimitationException() {
        super();
    }

    /**
     * Constructs a <code>FileSizeExceedsLimitationException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public FileSizeExceedsLimitationException(String message) {
        super(message);
    }
}
