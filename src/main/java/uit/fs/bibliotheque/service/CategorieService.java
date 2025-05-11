package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Categorie;
import uit.fs.bibliotheque.repository.CategorieRepository;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }
    
    public Page<Categorie> getAllCategories(Pageable pageable) {
        return categorieRepository.findAll(pageable);
    }
    
    public Page<Categorie> searchCategories(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return categorieRepository.findAll(pageable);
        }
        return categorieRepository.findByNomContainingIgnoreCase(searchTerm, pageable);
    }

    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    @Transactional
    public Categorie createCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Transactional
    public Categorie updateCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Transactional
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }


    public boolean existsByNom(String nom) {
        return categorieRepository.existsByNom(nom);
    }

}
