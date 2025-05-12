package uit.fs.bibliotheque.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

    public String saveImage(MultipartFile file, String classAppelant) throws IOException {
        Path callerDir = uploadDir.resolve(classAppelant);
        if (!Files.exists(callerDir)) {
            Files.createDirectories(callerDir);
        }
        
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = uuid + extension;
        
        Path filePath = callerDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return classAppelant + "/" + filename;
    }

    public byte[] getImage(String filename, String classAppelant) throws IOException {
        Path filePath = uploadDir.resolve(classAppelant).resolve(filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Image not found: " + classAppelant + "/" + filename);
        }
        return Files.readAllBytes(filePath);
    }

    public boolean deleteImage(String filename, String classAppelant) {
        if (filename != null && filename.startsWith(classAppelant + "/")) {
            filename = filename.substring((classAppelant + "/").length());
        }
        
        Path callerDir = uploadDir.resolve(classAppelant);
        if (!Files.exists(callerDir)) {
            return false; 
        }
        Path filePath = callerDir.resolve(filename);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    public Path getFullPath(String relativePath) {
        return uploadDir.resolve(relativePath);
    }
}
