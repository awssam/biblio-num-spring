package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Categorie;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.model.SliderLivre;
import uit.fs.bibliotheque.repository.LivreRepository;
import uit.fs.bibliotheque.repository.SectionAccueilRepository;
import uit.fs.bibliotheque.repository.SliderLivreRepository;

@Service
public class AccueilService {

    private final SliderLivreRepository sliderLivreRepository;
    private final SectionAccueilRepository sectionAccueilRepository;
    private final LivreRepository livreRepository;

    public AccueilService(SliderLivreRepository sliderLivreRepository, 
                        SectionAccueilRepository sectionAccueilRepository,
                        LivreRepository livreRepository) {
        this.sliderLivreRepository = sliderLivreRepository;
        this.sectionAccueilRepository = sectionAccueilRepository;
        this.livreRepository = livreRepository;
    }

    // =========== Méthodes pour SliderLivre ===========
    
    public List<SliderLivre> getAllSliderLivres() {
        return sliderLivreRepository.findAllByOrderByOrdreAsc();
    }
    
    public Optional<SliderLivre> getSliderLivreById(Long id) {
        return sliderLivreRepository.findById(id);
    }
    
    @Transactional
    public SliderLivre createSliderLivre(SliderLivre sliderLivre) {
        return sliderLivreRepository.save(sliderLivre);
    }
    
    @Transactional
    public SliderLivre updateSliderLivre(SliderLivre sliderLivre) {
        return sliderLivreRepository.save(sliderLivre);
    }
    
    @Transactional
    public void deleteSliderLivre(Long id) {
        sliderLivreRepository.deleteById(id);
    }
    
    // =========== Méthodes pour SectionAccueil ===========
    
    public List<SectionAccueil> getAllSectionsAccueil() {
        return sectionAccueilRepository.findAllByOrderByOrdreAsc();
    }
    
    public List<SectionAccueil> getSectionsAccueilByCategorie(Categorie categorie) {
        return sectionAccueilRepository.findByCategorieOrderByOrdreAsc(categorie);
    }
    
    public Optional<SectionAccueil> getSectionAccueilById(Long id) {
        return sectionAccueilRepository.findById(id);
    }
    
    @Transactional
    public SectionAccueil createSectionAccueil(SectionAccueil sectionAccueil) {
        return sectionAccueilRepository.save(sectionAccueil);
    }
    
    @Transactional
    public SectionAccueil updateSectionAccueil(SectionAccueil sectionAccueil) {
        return sectionAccueilRepository.save(sectionAccueil);
    }
    
    @Transactional
    public void deleteSectionAccueil(Long id) {
        sectionAccueilRepository.deleteById(id);
    }
    
    // =========== Méthodes pour récupérer les livres par section ===========
    
    public List<Livre> getLivresBySection(SectionAccueil section) {
        int size = section.getNombreLivres();
        Sort sort = Sort.by(Sort.Direction.DESC, "dateAjout");
        Pageable pageable = PageRequest.of(0, size, sort);
        
        if (section.getDisponiblesUniquement() != null && section.getDisponiblesUniquement()) {
            // À implémenter: obtenir livres disponibles uniquement
            // Pour le moment, on utilise getAllLivres
            return livreRepository.findAll(pageable).getContent();
        } else if (section.getCategorie() != null) {
            // À implémenter: obtenir livres par catégorie
            // Pour le moment, on utilise getAllLivres
            return livreRepository.findAll(pageable).getContent();
        } else {
            return livreRepository.findAll(pageable).getContent();
        }
    }
}
