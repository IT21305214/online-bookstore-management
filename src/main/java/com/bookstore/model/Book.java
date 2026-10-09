package com.bookstore.model;

public abstract class Book {

    // Encapsulation: private fields, accessed through getters/setters
    private String id;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private String coverImage;   // file name of the uploaded cover (may be empty)

    public Book(String id, String title, String author, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
    }

    // Abstraction: every child class MUST implement these
    public abstract String getType();
    public abstract double getExtraValue();
    public abstract String getExtraInfo();

    // File format: id|type|title|author|price|quantity|extra|coverImage
    public String toFileString() {
        return String.join("|",
                id, getType(), title, author,
                String.valueOf(price), String.valueOf(quantity),
                String.valueOf(getExtraValue()),
                coverImage == null ? "" : coverImage);
    }

    // URL the browser uses to show the cover (null when there is no cover)
    public String getCoverUrl() {
        return (coverImage == null || coverImage.isBlank()) ? null : "/covers/" + coverImage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}