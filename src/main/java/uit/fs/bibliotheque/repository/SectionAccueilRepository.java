package uit.fs.bibliotheque.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.Categorie;
import uit.fs.bibliotheque.model.SectionAccueil;

@Repository
public interface SectionAccueilRepository extends JpaRepository<SectionAccueil, Long> {
    
    List<SectionAccueil> findAllByOrderByOrdreAsc();
    
    List<SectionAccueil> findByCategorieOrderByOrdreAsc(Categorie categorie);
    
}
