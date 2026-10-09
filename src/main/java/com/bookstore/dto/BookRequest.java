package com.bookstore.dto;

// Holds the data sent from the HTML form (as JSON) to the backend
public class BookRequest {

    private String type;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private double extra;

    public BookRequest() {
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getExtra() { return extra; }
    public void setExtra(double extra) { this.extra = extra; }
}