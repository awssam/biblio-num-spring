// this shit is working never touch it again 
package uit.fs.bibliotheque.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class CustomPasswordEncoder implements PasswordEncoder {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomPasswordEncoder.class);
    
    private static final int SALT_LENGTH = 32;
    private static final int ITERATIONS = 390000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String DELIMITER = "$";
    private static final String PREFIX = "pbkdf2_sha256";
    private static final String PREDEFINED_SALT = "uB/jjvdcNl03sQrElmbsrXnokGIDCqtPDIVMpv+CmW8=";
    
    private final SecureRandom random;
    
    public CustomPasswordEncoder() {
        this.random = new SecureRandom();
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        return encodeWithPredefinedSalt(rawPassword);
    }
    public String encodeWithRandomSalt(CharSequence rawPassword) {
        byte[] salt = generateSalt();
        byte[] hash = hash(rawPassword.toString(), salt, ITERATIONS, KEY_LENGTH);
        
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        
        String encodedPassword = PREFIX + DELIMITER + 
               ITERATIONS + DELIMITER + 
               saltBase64 + DELIMITER +
               hashBase64;
               
        logger.debug("Password encoded. Algorithm: {}, Iterations: {}, Salt: {}***, Hash: {}***", 
                PREFIX, ITERATIONS, 
                maskString(saltBase64), 
                maskString(hashBase64));
                
        return encodedPassword;
    }

    public String encodeWithPredefinedSalt(CharSequence rawPassword) {
        byte[] salt = getPredefinedSalt();
        byte[] hash = hash(rawPassword.toString(), salt, ITERATIONS, KEY_LENGTH);
        
        String saltBase64 = PREDEFINED_SALT;
        String hashBase64 = Base64.getEncoder().encodeToString(hash);
        
        String encodedPassword = PREFIX + DELIMITER + 
               ITERATIONS + DELIMITER + 
               saltBase64 + DELIMITER +
               hashBase64;
               
        logger.debug("Password encoded with predefined salt. Algorithm: {}, Iterations: {}", PREFIX, ITERATIONS);
                
        return encodedPassword;
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            logger.warn("Empty encoded password provided");
            return false;
        }
        
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 4 || !parts[0].equals(PREFIX)) {
            logger.warn("Invalid encoded password format. Expected format: {}$iterations$salt$hash", PREFIX);
            return false;
        }
        
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] hash = Base64.getDecoder().decode(parts[3]);
            
            byte[] testHash = hash(rawPassword.toString(), salt, iterations, KEY_LENGTH);
            
            return constantTimeEquals(hash, testHash);
        } catch (Exception e) {
            logger.error("Error parsing password components", e);
            return false;
        }
    }
    
    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    private byte[] getPredefinedSalt() {
        return Base64.getDecoder().decode(PREDEFINED_SALT);
    }
    
    private byte[] hash(String password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), 
                salt,
                iterations, 
                keyLength
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
    
    private String maskString(String input) {
        if (input == null || input.length() <= 3) {
            return "***";
        }
        return input.substring(0, Math.min(input.length() / 2, 10)) + "***";
    }
}