package com.bookstore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

// Saves and deletes book cover images in the uploads/covers folder
@Service
public class CoverImageService {

    public static final String UPLOAD_DIR = "uploads/covers";
    private static final List<String> ALLOWED_TYPES = List.of("jpg", "jpeg", "png", "webp", "gif");

    private final Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    public CoverImageService() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the cover upload folder", e);
        }
    }

    // Saves the image and returns its new file name (or null if no file was chosen)
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!ALLOWED_TYPES.contains(extension)) {
            throw new IllegalArgumentException("Cover must be a JPG, PNG, WEBP or GIF image");
        }

        // Unique name so two books never overwrite each other's covers
        String fileName = UUID.randomUUID() + "." + extension;

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save the cover image", e);
        }
        return fileName;
    }

    // Deletes an old cover image file (ignored if it doesn't exist)
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) return;

        Path target = uploadPath.resolve(fileName).normalize();
        if (!target.startsWith(uploadPath)) return;   // safety: stay inside the folder

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            System.out.println("Could not delete cover image: " + fileName);
        }
    }
}