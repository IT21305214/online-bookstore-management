package com.bookstore.model;

public class BookFactory {

    public static Book create(String id, String type, String title, String author,
                              double price, int quantity, double extra) {
        if ("EBOOK".equalsIgnoreCase(type)) {
            return new EBook(id, title, author, price, quantity, extra);
        }
        return new PrintedBook(id, title, author, price, quantity, (int) extra);
    }
}