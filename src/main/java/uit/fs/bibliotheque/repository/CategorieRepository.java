package uit.fs.bibliotheque.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
        boolean existsByNom(String nom);
        Page<Categorie> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}