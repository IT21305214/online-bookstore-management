package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookFactory;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Spring automatically passes in the BookRepository (dependency injection)
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // READ - all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // READ - search by title or author
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllBooks();
        String k = keyword.trim().toLowerCase();
        return bookRepository.findAll().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(k)
                        || b.getAuthor().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }

    // READ - one book
    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    // CREATE + UPDATE - validate, then save
    public void saveBook(String id, String type, String title, String author,
                         double price, int quantity, double extra) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author is required");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (extra <= 0) throw new IllegalArgumentException("Pages / file size must be greater than 0");

        // New book: no ID yet, so generate one
        if (id == null || id.isBlank()) id = generateId();

        Book book = BookFactory.create(id, type, clean(title), clean(author), price, quantity, extra);
        bookRepository.save(book);
    }

    // DELETE
    public void deleteBook(String id) {
        if (!bookRepository.deleteById(id)) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
    }

    // "|" separates fields in the file, so it can't appear inside text
    private String clean(String text) {
        return text.trim().replace("|", "/");
    }

    // Example ID: B3F9A2C
    private String generateId() {
        return "B" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}