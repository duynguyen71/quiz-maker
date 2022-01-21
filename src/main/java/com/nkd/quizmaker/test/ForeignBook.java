package com.nkd.quizmaker.test;

import java.util.List;

public class ForeignBook extends Book {

    private String country;

    public ForeignBook(int id, String title, double price, String type, int publishYear, List<Author> authors, String country) {
        super(id, title, price, type, publishYear, authors);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean isForeignBook(String authName) {
        return this.containAuth(authName);
    }
}
