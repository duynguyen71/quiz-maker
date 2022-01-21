package com.nkd.quizmaker.test;

import java.util.List;

public class DomesticBook extends Book{

    public DomesticBook(int id, String title, double price, String type, int publishYear, List<Author> authors) {
        super(id, title, price, type, publishYear, authors);
    }

    @Override
    public boolean isForeignBook(String authName) {
        return false;
    }
}
