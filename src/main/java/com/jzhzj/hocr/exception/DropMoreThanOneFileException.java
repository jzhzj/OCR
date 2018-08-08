package com.jzhzj.hocr.exception;

public class DropMoreThanOneFileException extends Exception {
    /**
     * Constructs a <code>DropMoreThanOneFileException</code> with
     * <code>null</code> as its error detail message.
     */
    public DropMoreThanOneFileException() {
        super();
    }

    /**
     * Constructs a <code>DropMoreThanOneFileException</code> with the
     * specified detail message. The string <code>message</code> can be
     * retrieved later by the
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     *
     * @param message the detail message.
     */
    public DropMoreThanOneFileException(String message) {
        super(message);
    }
}
