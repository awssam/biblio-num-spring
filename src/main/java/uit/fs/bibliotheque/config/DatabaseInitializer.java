package uit.fs.bibliotheque.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import uit.fs.bibliotheque.model.RoleConstants;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.repository.UtilisateurRepository;
import uit.fs.bibliotheque.service.UtilisateurService;

/**
 * Component that initializes the database with essential data on application startup.
 * This includes creating an admin user if no users exist in the database.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:#{null}}")
    private String adminPassword;

    @Value("${admin.email:admin@biblio-num.com}")
    private String adminEmail;

    public DatabaseInitializer(
            UtilisateurRepository utilisateurRepository,
            UtilisateurService utilisateurService) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    /**
     * Create an admin user if no users exist in the database.
     * The admin password will be set from the environment variable or configuration property.
     * If not provided, a default strong password will be used.
     */
    private void initializeAdminUser() {
        if (utilisateurRepository.count() == 0) {
            logger.info("No users found in database. Creating admin user...");

            // Set default password if not provided in properties
            if (adminPassword == null || adminPassword.isEmpty()) {
                adminPassword = "Admin@123"; // Default strong password
                logger.warn("Admin password not set. Using default strong password.");
            }
            
            // Check if a user with the same username or email already exists
            Optional<Utilisateur> existingUser = utilisateurRepository.findByUsername(adminUsername);
            if (existingUser.isPresent()) {
                logger.info("Admin user already exists. Skipping creation.");
                return;
            }

            // Create the admin user
            Utilisateur admin = Utilisateur.builder()
                    .username(adminUsername)
                    .password(adminPassword) // Will be encrypted by the service
                    .email(adminEmail)
                    .firstName("Admin")
                    .lastName("User")
                    .role(RoleConstants.ADMINISTRATEUR)
                    .active(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();

            utilisateurService.createUtilisateur(admin);
            logger.info("Admin user created successfully with username: {}", adminUsername);
        } else {
            logger.info("Users found in database. Skipping admin user creation.");
        }
    }
}
