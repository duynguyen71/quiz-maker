package com.nkd.quizmaker.enumm;

import java.util.Arrays;
import java.util.Optional;

public enum EQuizStatus {

    PUBLIC(2), PRIVATE(1), DRAFT(0);

    private final int status;

    private EQuizStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static EQuizStatus findByValue(int value) {
        Optional<EQuizStatus> optional = Arrays.stream(EQuizStatus.values()).filter(s -> s.getStatus() == value).findFirst();
        if (optional.isPresent())
            return optional.get();
        return null;
    }
}
