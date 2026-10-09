package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.BookFactory;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CoverImageService coverImageService;

    // Spring automatically passes in both dependencies (dependency injection)
    public BookService(BookRepository bookRepository, CoverImageService coverImageService) {
        this.bookRepository = bookRepository;
        this.coverImageService = coverImageService;
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

    // CREATE + UPDATE - validate, save the cover image, then save the book
    public void saveBook(String id, String type, String title, String author,
                         double price, int quantity, double extra, MultipartFile cover) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author is required");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (extra <= 0) throw new IllegalArgumentException("Pages / file size must be greater than 0");

        boolean isNew = (id == null || id.isBlank());
        String coverImage = null;
        String oldCover = null;

        if (isNew) {
            id = generateId();
        } else {
            Book existing = getBookById(id);          // throws if the book doesn't exist
            coverImage = existing.getCoverImage();    // keep the current cover by default
            oldCover = coverImage;
        }

        // A new image was chosen: save it and use it
        String uploaded = coverImageService.save(cover);
        if (uploaded != null) coverImage = uploaded;

        Book book = BookFactory.create(id, type, clean(title), clean(author), price, quantity, extra);
        book.setCoverImage(coverImage);
        bookRepository.save(book);

        // Remove the replaced image file
        if (uploaded != null && oldCover != null) {
            coverImageService.delete(oldCover);
        }
    }

    // DELETE - remove the book and its cover image
    public void deleteBook(String id) {
        Book existing = getBookById(id);
        bookRepository.deleteById(id);
        coverImageService.delete(existing.getCoverImage());
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