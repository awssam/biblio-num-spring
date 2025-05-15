package uit.fs.bibliotheque.controller.admin;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import uit.fs.bibliotheque.controller.AbstractController;
import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.service.AuteurService;

@Controller
@RequestMapping("/dashboard/auteurs")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des auteurs dans le tableau de bord.
 */
public class AuteurDashboardController extends AbstractController {

    private final AuteurService auteurService;

    public AuteurDashboardController(AuteurService auteurService) {
        this.auteurService = auteurService;
    }


    @GetMapping({"", "/"})
    public String listeAuteurs(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        
        Page<Auteur> auteursPage;
        if (search != null && !search.trim().isEmpty()) {
            auteursPage = auteurService.searchAuteurs(search, pageable);
        } else {
            auteursPage = auteurService.getAllAuteurs(pageable);
        }
        
        model.addAttribute("auteurs", auteursPage.getContent());
        model.addAttribute("page", auteursPage);

        return renderView(model, "dashboard/auteurs/liste_auteurs", "Gestion des auteurs");
    }


    @GetMapping("/creer")
    public String afficherFormulaireCreationAuteur(Model model) {
        model.addAttribute("auteur", new Auteur());
        return renderView(model, "dashboard/auteurs/creer_auteur", "Créer un auteur");
    }


    @PostMapping("/creer")
    public String creerAuteur(
            @ModelAttribute @Valid Auteur auteur,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (auteur.getPrenom() == null || auteur.getPrenom().isEmpty()) {
            result.rejectValue("prenom", "error.auteur", "Le prénom de l'auteur ne peut pas être vide");
        }

        if (result.hasErrors()) {
            return renderView(model, "dashboard/auteurs/creer_auteur", "Créer un auteur");
        }

        auteurService.createAuteur(auteur);
        addSuccessMessage(redirectAttributes, "Auteur créée avec succès");
        return "redirect:/dashboard/auteurs";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationAuteur(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Auteur> auteurOpt = auteurService.getAuteurById(id);
        
        if (auteurOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Auteur non trouvé");
            return "redirect:/dashboard/auteurs";
        }
        
        model.addAttribute("auteur", auteurOpt.get());
        return renderView(model, "dashboard/auteurs/modifier_auteur", "Modifier l'auteur");
    }
    
    @PostMapping("/{id}/modifier")
    public String modifierAuteur(
            @PathVariable() Long id,
            @ModelAttribute @Valid Auteur auteur,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<Auteur> auteurExistanteOpt = auteurService.getAuteurById(id);
        
        if (auteurExistanteOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Auteur non trouvé");
            return "redirect:/dashboard/auteurs";
        }
        
        // Vérifier que le nom n'est pas vide
        if (auteur.getNom() == null || auteur.getNom().isEmpty()) {
            result.rejectValue("nom", "error.auteur", "Le nom de l'auteur ne peut pas être vide");
        }
        
        if (result.hasErrors()) {
            return renderView(model, "dashboard/auteurs/modifier_auteur", "Modifier l'auteur");
        }
        
        auteur.setId(id);
        auteurService.updateAuteur(auteur);
        
        addSuccessMessage(redirectAttributes, "Auteur modifiée avec succès");
        return "redirect:/dashboard/auteurs";
    }

    @GetMapping("/{id}/supprimer")
    public String afficherFormulaireSuppressionAuteur(@PathVariable() Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Auteur> auteurOpt = auteurService.getAuteurById(id);
        
        if (auteurOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Auteur non trouvé");
            return "redirect:/dashboard/auteurs";
        }
        
        model.addAttribute("auteur", auteurOpt.get());
        return renderView(model, "dashboard/auteurs/supprimer_auteur", "Supprimer l'auteur");
    }
    
    @PostMapping("/{id}/supprimer")
    public String supprimerAuteur(@PathVariable() Long id, RedirectAttributes redirectAttributes) {
        Optional<Auteur> auteurOpt = auteurService.getAuteurById(id);
        
        if (auteurOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Auteur non trouvé");
            return "redirect:/dashboard/auteurs";
        }
        
        try {
            auteurService.deleteAuteur(id);
            addSuccessMessage(redirectAttributes, "Auteur supprimée avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer cet auteur. il est peut-être assigné a des livres.");
        }
        
        return "redirect:/dashboard/auteurs";
    }

}