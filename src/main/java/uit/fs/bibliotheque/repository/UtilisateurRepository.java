package uit.fs.bibliotheque.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uit.fs.bibliotheque.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}