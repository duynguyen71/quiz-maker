package com.nkd.quizmaker.enumm;

public enum ESubmitType {

    FUN(1), EXAM(2);

    private int i;

    ESubmitType(int i) {
        this.i = i;
    }

    public int getType() {
        return this.i;
    }


}
