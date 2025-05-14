package uit.fs.bibliotheque.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.SliderLivre;
import uit.fs.bibliotheque.service.AccueilService;
import uit.fs.bibliotheque.service.LivreService;

@Controller
@RequestMapping("/dashboard/slider-livres")
@PreAuthorize("hasRole('MEMBRE') or hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des sliders de livres dans le tableau de bord.
 */
public class SliderLivreDashboardController extends AbstractController {

    private final AccueilService accueilService;
    private final LivreService livreService;

    public SliderLivreDashboardController(AccueilService accueilService, LivreService livreService) {
        this.accueilService = accueilService;
        this.livreService = livreService;
    }

    @GetMapping({"", "/"})
    public String listeSlidersLivres(Model model) {
        List<SliderLivre> slidersLivres = accueilService.getAllSliderLivres();
        model.addAttribute("slidersLivres", slidersLivres);
        return renderView(model, "dashboard/accueil/liste_sliders", "Gestion des sliders");
    }

    @GetMapping("/creer")
    public String afficherFormulaireCreationSlider(Model model) {
        model.addAttribute("sliderLivre", new SliderLivre());
        model.addAttribute("livres", livreService.getAllLivres());
        return renderView(model, "dashboard/accueil/creer_slider", "Ajouter un livre au slider");
    }

    @PostMapping("/creer")
    public String creerSliderLivre(
            @ModelAttribute("sliderLivre") @Valid SliderLivre sliderLivre,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (sliderLivre.getLivre() == null) {
            result.rejectValue("livre", "error.sliderLivre", "Vous devez sélectionner un livre");
        }
        
        if (sliderLivre.getOrdre() == null) {
            result.rejectValue("ordre", "error.sliderLivre", "L'ordre ne peut pas être vide");
        }

        if (result.hasErrors()) {
            model.addAttribute("livres", livreService.getAllLivres());
            return renderView(model, "dashboard/accueil/creer_slider", "Ajouter un livre au slider");
        }

        accueilService.createSliderLivre(sliderLivre);
        addSuccessMessage(redirectAttributes, "Livre ajouté au slider avec succès");
        return "redirect:/dashboard/slider-livres";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationSlider(
            @PathVariable() Long id, 
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<SliderLivre> sliderLivreOpt = accueilService.getSliderLivreById(id);
        
        if (sliderLivreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Slider non trouvé");
            return "redirect:/dashboard/slider-livres";
        }
        
        model.addAttribute("sliderLivre", sliderLivreOpt.get());
        model.addAttribute("livres", livreService.getAllLivres());
        return renderView(model, "dashboard/accueil/modifier_slider", "Modifier le slider");
    }
    
    @PostMapping("/{id}/modifier")
    public String modifierSliderLivre(
            @PathVariable() Long id,
            @ModelAttribute @Valid SliderLivre sliderLivre,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<SliderLivre> sliderLivreExistantOpt = accueilService.getSliderLivreById(id);
        
        if (sliderLivreExistantOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Slider non trouvé");
            return "redirect:/dashboard/slider-livres";
        }
        
        if (sliderLivre.getLivre() == null) {
            result.rejectValue("livre", "error.sliderLivre", "Vous devez sélectionner un livre");
        }
        
        if (sliderLivre.getOrdre() == null) {
            result.rejectValue("ordre", "error.sliderLivre", "L'ordre ne peut pas être vide");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("livres", livreService.getAllLivres());
            return renderView(model, "dashboard/accueil/modifier_slider", "Modifier le slider");
        }
        
        sliderLivre.setId(id);
        accueilService.updateSliderLivre(sliderLivre);
        
        addSuccessMessage(redirectAttributes, "Slider modifié avec succès");
        return "redirect:/dashboard/slider-livres";
    }

    @GetMapping("/{id}/supprimer")
    public String afficherFormulaireSuppressionSlider(
            @PathVariable() Long id, 
            Model model, 
            RedirectAttributes redirectAttributes) {
        Optional<SliderLivre> sliderLivreOpt = accueilService.getSliderLivreById(id);
        
        if (sliderLivreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Slider non trouvé");
            return "redirect:/dashboard/slider-livres";
        }
        
        model.addAttribute("sliderLivre", sliderLivreOpt.get());
        return renderView(model, "dashboard/accueil/supprimer_slider", "Supprimer du slider");
    }
    
    @PostMapping("/{id}/supprimer")
    public String supprimerSliderLivre(@PathVariable() Long id, RedirectAttributes redirectAttributes) {
        Optional<SliderLivre> sliderLivreOpt = accueilService.getSliderLivreById(id);
        
        if (sliderLivreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Slider non trouvé");
            return "redirect:/dashboard/slider-livres";
        }
        
        try {
            accueilService.deleteSliderLivre(id);
            addSuccessMessage(redirectAttributes, "Livre retiré du slider avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer ce slider. Une erreur est survenue.");
        }
        
        return "redirect:/dashboard/slider-livres";
    }
}
