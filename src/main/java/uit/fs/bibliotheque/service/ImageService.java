package uit.fs.bibliotheque.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final Path uploadDir;

    public ImageService(@Value("${spring.servlet.multipart.location}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath);
        if (!Files.exists(this.uploadDir)) {
            try {
                Files.createDirectories(this.uploadDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
    }

    // Upload and save the image file
    public String saveImage(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filename; // Return filename to be stored in DB
    }

    // Get image bytes (e.g., for API serving if needed)
    public byte[] getImage(String filename) throws IOException {
        Path filePath = uploadDir.resolve(filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Image not found: " + filename);
        }
        return Files.readAllBytes(filePath);
    }

    // Delete an image
    public boolean deleteImage(String filename) {
        Path filePath = uploadDir.resolve(filename);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
