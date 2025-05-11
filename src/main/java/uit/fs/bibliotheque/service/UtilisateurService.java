package uit.fs.bibliotheque.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import uit.fs.bibliotheque.model.RoleConstants;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.repository.UtilisateurRepository;

@Service
public class UtilisateurService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
    }

    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        // Ensure the password is encoded
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        
        // Set default role if not provided
        if (utilisateur.getRole() == null || utilisateur.getRole().isEmpty()) {
            utilisateur.setRole(RoleConstants.MEMBRE);
        }
        
        return utilisateurRepository.save(utilisateur);
    }

    public boolean existsByUsername(String username) {
        return utilisateurRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.findByEmail(email).isPresent();
    }

    public Utilisateur updateUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public boolean checkPassword(Utilisateur utilisateur, String rawPassword) {
        return passwordEncoder.matches(rawPassword, utilisateur.getPassword());
    }

    public Utilisateur changePassword(Utilisateur utilisateur, String newPassword) {
        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        return utilisateurRepository.save(utilisateur);
    }
}