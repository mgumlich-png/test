package com.example.gks.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class FileStorageService {

    private final Path basePath;

    public FileStorageService(@Value("${storage.base-path:documents}") String basePath) throws IOException {
        this.basePath = Paths.get(basePath);
        Files.createDirectories(this.basePath);
    }

    public String store(MultipartFile file, String prefix) throws IOException {
        String filename = prefix + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path destination = basePath.resolve(filename);
        Files.copy(file.getInputStream(), destination);
        return destination.toString();
    }

    public String hashFile(Path path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(readBytes(path));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new IllegalStateException("Unable to hash file", e);
        }
    }

    private byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public void clear() throws IOException {
        FileSystemUtils.deleteRecursively(basePath);
        Files.createDirectories(basePath);
    }
}
