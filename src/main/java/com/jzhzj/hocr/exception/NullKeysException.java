package com.jzhzj.hocr.exception;

public class NullKeysException extends Exception {
    /**
     * Constructs a <code>NullKeysException</code> with
     * <code>null</code> as its error detail message.
     */
    public NullKeysException() {
        super();
    }

    /**
     * Constructs a <code>NullKeysException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public NullKeysException(String message) {
        super(message);
    }
}
