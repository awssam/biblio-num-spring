package uit.fs.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uit.fs.bibliotheque.model.ConfigurationEmprunt;

@Repository
public interface ConfigurationEmpruntRepository extends JpaRepository<ConfigurationEmprunt, Long> {
}
