package uit.fs.bibliotheque.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    private final String uploadDir;
    private final String baseUploadUrl;
    private final String defaultAvatarUrl;

    public FileStorageConfig(
            @Value("${spring.servlet.multipart.location}") String uploadDir,
            @Value("${app.base-upload-url:/uploads/}") String baseUploadUrl,
            @Value("${app.default-avatar:/images/default-avatar.png}") String defaultAvatarUrl
    ) {
        this.uploadDir = uploadDir;
        this.baseUploadUrl = baseUploadUrl;
        this.defaultAvatarUrl = defaultAvatarUrl;
        ensureUploadDirectoryExists();
    }

    // ⬇️ Crée le dossier upload au démarrage
    private void ensureUploadDirectoryExists() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // ⬇️ Expose le dossier /uploads comme ressource statique
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    // ⬇️ Fournit une implémentation de config d’URL d’image
    @Bean
    public ImageUrlConfig imageUrlConfig() {
        return new ImageUrlConfig(baseUploadUrl, defaultAvatarUrl);
    }
}
