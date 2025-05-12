package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.repository.LivreRepository;

@Service
public class LivreService {

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }
    
    public Page<Livre> getAllLivres(Pageable pageable) {
        return livreRepository.findAll(pageable);
    }
    
    public Page<Livre> searchLivres(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return livreRepository.findAll(pageable);
        }
        return livreRepository.findByTitre(searchTerm,pageable);
    }

    public Optional<Livre> getLivreById(Long id) {
        return livreRepository.findById(id);
    }

    @Transactional
    public Livre createLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    @Transactional
    public Livre updateLivre(Livre livre) {
        return livreRepository.save(livre);
    }

    @Transactional
    public void deleteLivre(Long id) {
        livreRepository.deleteById(id);
    }




}
