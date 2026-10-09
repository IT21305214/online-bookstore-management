package com.bookstore.repository;

import com.bookstore.model.Book;
import com.bookstore.model.BookFactory;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    // Books are saved in this text file inside the project folder
    private static final String FILE_PATH = "data/books.txt";

    // Creates the data folder and file the first time the app runs
    public BookRepository() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            if (!Files.exists(path)) Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create data file", e);
        }
    }

    // READ - get all books from the file
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                Book book = parseLine(line);
                if (book != null) books.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    // READ - get one book by its ID
    public Optional<Book> findById(String id) {
        return findAll().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    // CREATE + UPDATE - add a new book or replace an existing one
    public synchronized void save(Book book) {
        List<Book> books = findAll();
        boolean updated = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equals(book.getId())) {
                books.set(i, book);
                updated = true;
                break;
            }
        }
        if (!updated) books.add(book);
        writeAll(books);
    }

    // DELETE - remove a book by its ID
    public synchronized boolean deleteById(String id) {
        List<Book> books = findAll();
        boolean removed = books.removeIf(b -> b.getId().equals(id));
        if (removed) writeAll(books);
        return removed;
    }

    // Writes the whole list back to the file
    private void writeAll(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book b : books) {
                writer.write(b.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save books", e);
        }
    }

    // Turns one line of the file back into a Book object
    // Line format: id|type|title|author|price|quantity|extra|coverImage
    private Book parseLine(String line) {
        try {
            String[] p = line.split("\\|");
            if (p.length < 7) return null;

            Book book = BookFactory.create(
                    p[0], p[1], p[2], p[3],
                    Double.parseDouble(p[4]),
                    Integer.parseInt(p[5]),
                    Double.parseDouble(p[6]));

            // Older lines have no cover field, so it's optional
            if (p.length >= 8 && !p[7].isBlank()) {
                book.setCoverImage(p[7]);
            }
            return book;
        } catch (NumberFormatException e) {
            System.out.println("Skipping bad line: " + line);
            return null;
        }
    }
}