package com.example.haseka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${app.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String fileName = UUID.randomUUID() + extension;
        Path targetDirectory = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
        Files.createDirectories(targetDirectory);

        Path targetFile = targetDirectory.resolve(fileName).normalize();
        if (!targetFile.startsWith(targetDirectory)) {
            throw new IOException("Invalid upload path.");
        }

        file.transferTo(targetFile);
        return "/uploads/" + folder + "/" + fileName;
    }
}
