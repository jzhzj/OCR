package com.jzhzj.hocr.exception;

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
