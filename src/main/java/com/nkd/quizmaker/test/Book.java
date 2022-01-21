package com.nkd.quizmaker.test;

import java.util.ArrayList;
import java.util.List;

public abstract class Book {

    protected int id;

    protected String title;

    protected double price;

    protected String type;

    protected int publishYear;

    protected List<Author> authors;

    public Book(int id, String title, double price, String type, int publishYear, List<Author> authors) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.type = type;
        this.publishYear = publishYear;
        this.authors = authors;
    }

    //kiem tra sach nuoc ngoai
    public abstract boolean isForeignBook(String authName);

    //kiểm tra tồn tại tác giả trong danh sách không
    public boolean containAuth(String authorName) {
        for (int i = 0; i < this.authors.size(); i++) {
            Author auth = this.authors.get(i);
            if (auth.getName().equals(authorName)) {
                return true;
            }
        }
        return false;
    }

    public Book() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
