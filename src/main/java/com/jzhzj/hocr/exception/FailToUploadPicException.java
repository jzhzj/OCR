package com.jzhzj.hocr.exception;

/**
 * Thrown to indicate that failed to upload the file to the cloud.
 * Either bad connection to the Internet or banned by firewalls.
 *
 * @author jzhzj
 */
public class FailToUploadPicException extends Exception {
    /**
     * Constructs a <code>FailToUploadPicException</code> with
     * <code>null</code> as its error detail message.
     */
    public FailToUploadPicException() {
        super();
    }

    /**
     * Constructs a <code>FailToUploadPicException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public FailToUploadPicException(String message) {
        super(message);
    }
}
