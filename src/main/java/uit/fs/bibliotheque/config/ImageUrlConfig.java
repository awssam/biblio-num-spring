package uit.fs.bibliotheque.config;

public class ImageUrlConfig {

    private final String baseUploadUrl;
    private final String defaultAvatarUrl;

    public ImageUrlConfig(String baseUploadUrl, String defaultAvatarUrl) {
        this.baseUploadUrl = baseUploadUrl;
        this.defaultAvatarUrl = defaultAvatarUrl;
    }

    public String getProfileImageUrl(String imageName) {
        return (imageName != null && !imageName.isEmpty())
                ? baseUploadUrl + imageName
                : defaultAvatarUrl;
    }

    public String getBaseUploadUrl() {
        return baseUploadUrl;
    }

    public String getDefaultAvatarUrl() {
        return defaultAvatarUrl;
    }
}
