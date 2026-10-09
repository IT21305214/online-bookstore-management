package com.bookstore.model;

public class EBook extends Book {

    private static final double DIGITAL_DISCOUNT = 0.10;
    private double fileSizeMb;

    public EBook(String id, String title, String author,
                 double price, int quantity, double fileSizeMb) {
        super(id, title, author, price, quantity);
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    public String getType() { return "EBOOK"; }

    // Polymorphism: e-books get a 10% discount
    @Override
    public double getFinalPrice() { return getPrice() * (1 - DIGITAL_DISCOUNT); }

    @Override
    public double getExtraValue() { return fileSizeMb; }

    @Override
    public String getExtraInfo() { return fileSizeMb + " MB"; }

    public double getFileSizeMb() { return fileSizeMb; }
    public void setFileSizeMb(double fileSizeMb) { this.fileSizeMb = fileSizeMb; }
}