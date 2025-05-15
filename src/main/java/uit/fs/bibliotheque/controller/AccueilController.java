package uit.fs.bibliotheque.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.service.AccueilService;

@Controller
public class AccueilController extends AbstractController {

    private final AccueilService accueilService;

    public AccueilController(AccueilService accueilService) {
        this.accueilService = accueilService;
    }

    @GetMapping("/")
    public String accueil(Model model) {
        List<Livre> slidersLivres = accueilService.livresSlider();
        model.addAttribute("livresSlider", slidersLivres);
        
        List<SectionAccueil> sectionsAccueil = accueilService.getAllSectionsAccueil();
        model.addAttribute("sectionsAccueil", sectionsAccueil);

        
        return renderView(model, "index", "Bibliothèque Numérique");
    }
}
