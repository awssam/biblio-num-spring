package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.repository.AuteurRepository;

@Service
public class AuteurService {

    private final AuteurRepository auteurRepository;

    public AuteurService(AuteurRepository auteurRepository) {
        this.auteurRepository = auteurRepository;
    }

    public List<Auteur> getAllAuteurs() {
        return auteurRepository.findAll();
    }
    
    public Page<Auteur> getAllAuteurs(Pageable pageable) {
        return auteurRepository.findAll(pageable);
    }
    
    public Page<Auteur> searchAuteurs(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return auteurRepository.findAll(pageable);
        }
        return auteurRepository.findByNomOrPrenom(searchTerm, searchTerm,pageable);
    }

    public Optional<Auteur> getAuteurById(Long id) {
        return auteurRepository.findById(id);
    }

    @Transactional
    public Auteur createAuteur(Auteur auteur) {
        return auteurRepository.save(auteur);
    }

    @Transactional
    public Auteur updateAuteur(Auteur auteur) {
        return auteurRepository.save(auteur);
    }

    @Transactional
    public void deleteAuteur(Long id) {
        auteurRepository.deleteById(id);
    }




}
