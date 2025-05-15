package uit.fs.bibliotheque.service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.model.SliderLivre;
import uit.fs.bibliotheque.repository.SectionAccueilRepository;
import uit.fs.bibliotheque.repository.SliderLivreRepository;
 
@Service
public class AccueilService {

    private final SliderLivreRepository sliderLivreRepository;
    private final SectionAccueilRepository sectionAccueilRepository;

    public AccueilService(SliderLivreRepository sliderLivreRepository, 
                        SectionAccueilRepository sectionAccueilRepository) {
        this.sliderLivreRepository = sliderLivreRepository;
        this.sectionAccueilRepository = sectionAccueilRepository;
    }
    
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
    
    
    public List<SectionAccueil> getAllSectionsAccueil() {
        return sectionAccueilRepository.findAllByOrderByOrdreAsc();
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
    public List<Livre> livresSlider() {
        return sliderLivreRepository.findAllByOrderByOrdreAsc().stream().map(SliderLivre::getLivre).toList();
    }
}
