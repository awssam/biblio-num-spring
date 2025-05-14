package uit.fs.bibliotheque.controller.admin;

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
import uit.fs.bibliotheque.controller.AbstractController;
import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.service.AccueilService;
import uit.fs.bibliotheque.service.CategorieService;

@Controller
@RequestMapping("/dashboard/sections-accueil")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des sections d'accueil dans le tableau de bord.
 */
public class SectionAccueilDashboardController extends AbstractController {

    private final AccueilService accueilService;
    private final CategorieService categorieService;

    public SectionAccueilDashboardController(
            AccueilService accueilService, 
            CategorieService categorieService) {
        this.accueilService = accueilService;
        this.categorieService = categorieService;
    }

    @GetMapping({"", "/"})
    public String listeSectionsAccueil(Model model) {
        List<SectionAccueil> sectionsAccueil = accueilService.getAllSectionsAccueil();
        model.addAttribute("sectionsAccueil", sectionsAccueil);
        return renderView(model, "dashboard/accueil/liste_sections", "Gestion des sections");
    }

    @GetMapping("/creer")
    public String afficherFormulaireCreationSection(Model model) {
        SectionAccueil sectionAccueil = new SectionAccueil();
        // Valeurs par défaut
        sectionAccueil.setNombreLivres(6);
        sectionAccueil.setLivresParPage(3);
        sectionAccueil.setIntervalleDefilement(5000); // 5 secondes
        sectionAccueil.setDisponiblesUniquement(true);
        
        model.addAttribute("sectionAccueil", sectionAccueil);
        model.addAttribute("categories", categorieService.getAllCategories());
        return renderView(model, "dashboard/accueil/creer_section", "Créer une section");
    }

    @PostMapping("/creer")
    public String creerSectionAccueil(
            @ModelAttribute("sectionAccueil") @Valid SectionAccueil sectionAccueil,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (sectionAccueil.getTitre() == null || sectionAccueil.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.sectionAccueil", "Le titre de la section ne peut pas être vide");
        }
        
        if (sectionAccueil.getOrdre() == null) {
            result.rejectValue("ordre", "error.sectionAccueil", "L'ordre de la section ne peut pas être vide");
        }
        
        if (sectionAccueil.getNombreLivres() == null || sectionAccueil.getNombreLivres() <= 0) {
            result.rejectValue("nombreLivres", "error.sectionAccueil", "Le nombre de livres à afficher doit être positif");
        }
        
        if (sectionAccueil.getLivresParPage() == null || sectionAccueil.getLivresParPage() <= 0) {
            result.rejectValue("livresParPage", "error.sectionAccueil", "Le nombre de livres par page doit être positif");
        }
        
        if (sectionAccueil.getIntervalleDefilement() == null || sectionAccueil.getIntervalleDefilement() < 1000) {
            result.rejectValue("intervalleDefilement", "error.sectionAccueil", "L'intervalle de défilement doit être au moins 1000ms");
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categorieService.getAllCategories());
            return renderView(model, "dashboard/accueil/creer_section", "Créer une section");
        }

        accueilService.createSectionAccueil(sectionAccueil);
        addSuccessMessage(redirectAttributes, "Section créée avec succès");
        return "redirect:/dashboard/sections-accueil";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationSection(
            @PathVariable() Long id, 
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<SectionAccueil> sectionAccueilOpt = accueilService.getSectionAccueilById(id);
        
        if (sectionAccueilOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Section non trouvée");
            return "redirect:/dashboard/sections-accueil";
        }
        
        model.addAttribute("sectionAccueil", sectionAccueilOpt.get());
        model.addAttribute("categories", categorieService.getAllCategories());
        return renderView(model, "dashboard/accueil/modifier_section", "Modifier la section");
    }
    
    @PostMapping("/{id}/modifier")
    public String modifierSectionAccueil(
            @PathVariable() Long id,
            @ModelAttribute @Valid SectionAccueil sectionAccueil,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<SectionAccueil> sectionAccueilExistanteOpt = accueilService.getSectionAccueilById(id);
        
        if (sectionAccueilExistanteOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Section non trouvée");
            return "redirect:/dashboard/sections-accueil";
        }
        
        if (sectionAccueil.getTitre() == null || sectionAccueil.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.sectionAccueil", "Le titre de la section ne peut pas être vide");
        }
        
        if (sectionAccueil.getOrdre() == null) {
            result.rejectValue("ordre", "error.sectionAccueil", "L'ordre de la section ne peut pas être vide");
        }
        
        if (sectionAccueil.getNombreLivres() == null || sectionAccueil.getNombreLivres() <= 0) {
            result.rejectValue("nombreLivres", "error.sectionAccueil", "Le nombre de livres à afficher doit être positif");
        }
        
        if (sectionAccueil.getLivresParPage() == null || sectionAccueil.getLivresParPage() <= 0) {
            result.rejectValue("livresParPage", "error.sectionAccueil", "Le nombre de livres par page doit être positif");
        }
        
        if (sectionAccueil.getIntervalleDefilement() == null || sectionAccueil.getIntervalleDefilement() < 1000) {
            result.rejectValue("intervalleDefilement", "error.sectionAccueil", "L'intervalle de défilement doit être au moins 1000ms");
        }
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categorieService.getAllCategories());
            return renderView(model, "dashboard/accueil/modifier_section", "Modifier la section");
        }
        
        sectionAccueil.setId(id);
        accueilService.updateSectionAccueil(sectionAccueil);
        
        addSuccessMessage(redirectAttributes, "Section modifiée avec succès");
        return "redirect:/dashboard/sections-accueil";
    }

    @GetMapping("/{id}/supprimer")
    public String afficherFormulaireSuppressionSection(
            @PathVariable() Long id, 
            Model model, 
            RedirectAttributes redirectAttributes) {
        Optional<SectionAccueil> sectionAccueilOpt = accueilService.getSectionAccueilById(id);
        
        if (sectionAccueilOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Section non trouvée");
            return "redirect:/dashboard/sections-accueil";
        }
        
        model.addAttribute("sectionAccueil", sectionAccueilOpt.get());
        return renderView(model, "dashboard/accueil/supprimer_section", "Supprimer une section");
    }
    
    @PostMapping("/{id}/supprimer")
    public String supprimerSectionAccueil(
            @PathVariable() Long id, 
            RedirectAttributes redirectAttributes) {
        Optional<SectionAccueil> sectionAccueilOpt = accueilService.getSectionAccueilById(id);
        
        if (sectionAccueilOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Section non trouvée");
            return "redirect:/dashboard/sections-accueil";
        }
        
        try {
            accueilService.deleteSectionAccueil(id);
            addSuccessMessage(redirectAttributes, "Section supprimée avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer cette section. Une erreur est survenue.");
        }
        
        return "redirect:/dashboard/sections-accueil";
    }
}
