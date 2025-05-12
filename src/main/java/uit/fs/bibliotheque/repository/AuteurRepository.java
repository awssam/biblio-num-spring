package uit.fs.bibliotheque.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.Auteur;

@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {
    
    Page<Auteur> findByNomOrPrenom(String nom, String prenom, Pageable pageable);

}