package com.nkd.quizmaker.exception;

public class OptionNotFoundException extends Exception {

    public OptionNotFoundException(String message) {
        super(message);
    }

    public OptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
