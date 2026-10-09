package com.bookstore.model;

public class PrintedBook extends Book {

    private static final double DELIVERY_FEE = 350.0;
    private int pages;

    public PrintedBook(String id, String title, String author,
                       double price, int quantity, int pages) {
        super(id, title, author, price, quantity);
        this.pages = pages;
    }

    @Override
    public String getType() { return "PRINTED"; }

    // Polymorphism: printed books add a delivery fee
    @Override
    public double getFinalPrice() { return getPrice() + DELIVERY_FEE; }

    @Override
    public double getExtraValue() { return pages; }

    @Override
    public String getExtraInfo() { return pages + " pages"; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}