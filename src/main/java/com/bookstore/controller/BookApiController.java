package com.bookstore.controller;

import com.bookstore.dto.BookRequest;
import com.bookstore.model.Book;
import com.bookstore.model.EBook;
import com.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;

    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    // READ - all books, or search:  GET /api/books?keyword=harry
    @GetMapping
    public List<Book> getBooks(@RequestParam(value = "keyword", required = false) String keyword) {
        return bookService.searchBooks(keyword);
    }

    // READ - dashboard numbers:  GET /api/books/stats
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        List<Book> books = bookService.getAllBooks();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", books.size());
        stats.put("totalStock", books.stream().mapToInt(Book::getQuantity).sum());
        stats.put("ebookCount", books.stream().filter(b -> b instanceof EBook).count());
        return stats;
    }

    // READ - one book:  GET /api/books/B1234
    @GetMapping("/{id}")
    public Book getBook(@PathVariable("id") String id) {
        return bookService.getBookById(id);
    }

    // CREATE:  POST /api/books
    @PostMapping
    public ResponseEntity<Map<String, String>> createBook(@RequestBody BookRequest request) {
        bookService.saveBook(null, request.getType(), request.getTitle(), request.getAuthor(),
                request.getPrice(), request.getQuantity(), request.getExtra());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Book added successfully!"));
    }

    // UPDATE:  PUT /api/books/B1234
    @PutMapping("/{id}")
    public Map<String, String> updateBook(@PathVariable("id") String id,
                                          @RequestBody BookRequest request) {
        bookService.getBookById(id); // throws an error if the book doesn't exist
        bookService.saveBook(id, request.getType(), request.getTitle(), request.getAuthor(),
                request.getPrice(), request.getQuantity(), request.getExtra());
        return Map.of("message", "Book updated successfully!");
    }

    // DELETE:  DELETE /api/books/B1234
    @DeleteMapping("/{id}")
    public Map<String, String> deleteBook(@PathVariable("id") String id) {
        bookService.deleteBook(id);
        return Map.of("message", "Book deleted successfully!");
    }

    // Sends validation / not-found errors back to the page as JSON
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleError(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}