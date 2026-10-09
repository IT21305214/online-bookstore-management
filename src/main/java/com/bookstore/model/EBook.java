package com.bookstore.model;

public class EBook extends Book {

    private double fileSizeMb;

    public EBook(String id, String title, String author,
                 double price, int quantity, double fileSizeMb) {
        super(id, title, author, price, quantity);
        this.fileSizeMb = fileSizeMb;
    }

    // Polymorphism: each book type gives its own answer
    @Override
    public String getType() { return "EBOOK"; }

    @Override
    public double getExtraValue() { return fileSizeMb; }

    @Override
    public String getExtraInfo() { return fileSizeMb + " MB"; }

    public double getFileSizeMb() { return fileSizeMb; }
    public void setFileSizeMb(double fileSizeMb) { this.fileSizeMb = fileSizeMb; }
}