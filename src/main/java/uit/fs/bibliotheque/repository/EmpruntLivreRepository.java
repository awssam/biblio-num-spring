package uit.fs.bibliotheque.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.EmpruntLivre;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.model.EmpruntLivre.StatutEmprunt;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmpruntLivreRepository extends JpaRepository<EmpruntLivre, Long> {
    
    // Méthodes pour récupérer des listes d'emprunts
    List<EmpruntLivre> findByUtilisateurOrderByDateEmpruntDesc(Utilisateur utilisateur);
    
    List<EmpruntLivre> findByLivreOrderByDateEmpruntDesc(Livre livre);
    
    List<EmpruntLivre> findByStatutOrderByDateEmpruntDesc(StatutEmprunt statut);
    
    List<EmpruntLivre> findByStatutAndDateRetourPrevueBefore(StatutEmprunt statut, LocalDateTime dateLimit);
    
    // Méthodes de comptage
    long countByUtilisateurAndStatut(Utilisateur utilisateur, StatutEmprunt statut);
    
    long countByLivreAndStatut(Livre livre, StatutEmprunt statut);
    
    long countByStatut(StatutEmprunt statut);
    
    // Méthodes de vérification
    boolean existsByUtilisateurAndLivreAndStatut(Utilisateur utilisateur, Livre livre, StatutEmprunt statut);
    
    // Méthodes paginées pour le contrôleur
    Page<EmpruntLivre> findByUtilisateur(Utilisateur utilisateur, Pageable pageable);
    
    Page<EmpruntLivre> findByUtilisateurAndStatut(Utilisateur utilisateur, StatutEmprunt statut, Pageable pageable);
    
    Page<EmpruntLivre> findByStatut(StatutEmprunt statut, Pageable pageable);
    
    Page<EmpruntLivre> findByLivreId(Long livreId, Pageable pageable);
    
    Page<EmpruntLivre> findByUtilisateurId(Long utilisateurId, Pageable pageable);
    
    Page<EmpruntLivre> findByStatutAndUtilisateurId(StatutEmprunt statut, Long utilisateurId, Pageable pageable);
    
    Page<EmpruntLivre> findByStatutAndLivreId(StatutEmprunt statut, Long livreId, Pageable pageable);
    
    Page<EmpruntLivre> findByUtilisateurIdAndLivreId(Long utilisateurId, Long livreId, Pageable pageable);
    
    Page<EmpruntLivre> findByStatutAndUtilisateurIdAndLivreId(StatutEmprunt statut, Long utilisateurId, Long livreId, Pageable pageable);
}
