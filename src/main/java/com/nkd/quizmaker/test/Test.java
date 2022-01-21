package com.nkd.quizmaker.test;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Author author = new Author("Author1", 2001);
        Author author2 = new Author("Author2", 2000);

        List<Author> authors = new ArrayList<>();
        authors.add(author);
        authors.add(author2);

        Book domesticBook = new DomesticBook(1, "Title1", 100, "a", 2010, authors);
        Book foreignBook = new ForeignBook(1, "Title1", 100, "a", 2010, authors, "USA");

        System.out.println(domesticBook.isForeignBook("hih"));
        System.out.println(foreignBook.isForeignBook("Author1"));
    }
}
