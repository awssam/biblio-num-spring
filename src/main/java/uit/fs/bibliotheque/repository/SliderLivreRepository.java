package uit.fs.bibliotheque.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.SliderLivre;

@Repository
public interface SliderLivreRepository extends JpaRepository<SliderLivre, Long> {
    
    List<SliderLivre> findAllByOrderByOrdreAsc();
    
}
