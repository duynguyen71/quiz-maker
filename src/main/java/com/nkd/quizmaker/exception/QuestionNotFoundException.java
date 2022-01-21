package com.nkd.quizmaker.exception;

public class QuestionNotFoundException extends Exception {

    public QuestionNotFoundException(String message) {
        super(message);
    }

    public QuestionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
