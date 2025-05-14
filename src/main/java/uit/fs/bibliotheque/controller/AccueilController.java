package uit.fs.bibliotheque.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.model.SliderLivre;
import uit.fs.bibliotheque.service.AccueilService;

@Controller
public class AccueilController extends AbstractController {

    private final AccueilService accueilService;

    public AccueilController(AccueilService accueilService) {
        this.accueilService = accueilService;
    }

    @GetMapping("/")
    public String accueil(Model model) {
        // Récupérer les sliders pour le carrousel principal
        List<SliderLivre> slidersLivres = accueilService.getAllSliderLivres();
        model.addAttribute("slidersLivres", slidersLivres);
        
        // Récupérer les sections configurées
        List<SectionAccueil> sectionsAccueil = accueilService.getAllSectionsAccueil();
        model.addAttribute("sectionsAccueil", sectionsAccueil);
        
        // Pour chaque section, récupérer les livres correspondants
        for (SectionAccueil section : sectionsAccueil) {
            String attributeName = "livresSection" + section.getId();
            model.addAttribute(attributeName, accueilService.getLivresBySection(section));
        }
        
        return renderView(model, "index", "Bibliothèque Numérique");
    }
}
