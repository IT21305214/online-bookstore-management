package com.bookstore.model;

public class PrintedBook extends Book {

    private int pages;

    public PrintedBook(String id, String title, String author,
                       double price, int quantity, int pages) {
        super(id, title, author, price, quantity);
        this.pages = pages;
    }

    // Polymorphism: each book type gives its own answer
    @Override
    public String getType() { return "PRINTED"; }

    @Override
    public double getExtraValue() { return pages; }

    @Override
    public String getExtraInfo() { return pages + " pages"; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}