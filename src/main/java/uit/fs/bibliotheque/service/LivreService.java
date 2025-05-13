package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.model.Categorie;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.repository.LivreRepository;

@Service
public class LivreService {

    private final LivreRepository livreRepository;
    private final AuteurService auteurService;
    private final CategorieService categorieService;

    public LivreService(LivreRepository livreRepository, AuteurService auteurService, CategorieService categorieService) {
        this.livreRepository = livreRepository;
        this.auteurService = auteurService;
        this.categorieService = categorieService;
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
        return livreRepository.findByTitre(searchTerm, pageable);
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

    @Transactional
    public Livre addAuteurToLivre(Long livreId, Long auteurId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        Auteur auteur = auteurService.getAuteurById(auteurId)
            .orElseThrow(() -> new RuntimeException("Auteur non trouvé"));
        
        livre.addAuteur(auteur);
        return livreRepository.save(livre);
    }


    @Transactional
    public Livre removeAuteurFromLivre(Long livreId, Long auteurId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        Auteur auteur = auteurService.getAuteurById(auteurId)
            .orElseThrow(() -> new RuntimeException("Auteur non trouvé"));
        
        livre.removeAuteur(auteur);
        return livreRepository.save(livre);
    }

    public Set<Auteur> getLivreAuteurs(Long livreId) {
        return livreRepository.findById(livreId)
            .map(livre -> livre.getAuteurs())
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
    }

    @Transactional
    public Livre addCategorieToLivre(Long livreId, Long categorieId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        Categorie categorie = categorieService.getCategorieById(categorieId)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        livre.addCategorie(categorie);
        return livreRepository.save(livre);
    }

    @Transactional
    public Livre removeCategorieFromLivre(Long livreId, Long categorieId) {
        Livre livre = livreRepository.findById(livreId)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        Categorie categorie = categorieService.getCategorieById(categorieId)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        livre.removeCategorie(categorie);
        return livreRepository.save(livre);
    }
    
    public Set<Categorie> getLivreCategories(Long livreId) {
        return livreRepository.findById(livreId)
            .map(livre -> livre.getCategories())
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
    }
}
