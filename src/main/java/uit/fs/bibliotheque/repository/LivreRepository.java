package uit.fs.bibliotheque.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    
    Page<Livre> findByTitre(String titre, Pageable pageable);

}