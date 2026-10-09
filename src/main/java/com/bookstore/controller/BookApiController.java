package com.bookstore.controller;

import com.bookstore.dto.BookRequest;
import com.bookstore.model.Book;
import com.bookstore.model.EBook;
import com.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // CREATE (with optional cover image):  POST /api/books
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createBook(
            @ModelAttribute BookRequest request,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {

        bookService.saveBook(null, request.getType(), request.getTitle(), request.getAuthor(),
                request.getPrice(), request.getQuantity(), request.getExtra(), cover);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Book added successfully!"));
    }

    // UPDATE (with optional new cover image):  POST /api/books/B1234
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> updateBook(
            @PathVariable("id") String id,
            @ModelAttribute BookRequest request,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {

        bookService.saveBook(id, request.getType(), request.getTitle(), request.getAuthor(),
                request.getPrice(), request.getQuantity(), request.getExtra(), cover);
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