package uit.fs.bibliotheque.controller;

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
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.service.LivreService;

@Controller
@RequestMapping("/dashboard/livres")
@PreAuthorize("hasRole('MEMBRE') or hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des livres dans le tableau de bord.
 */
public class LivreDashboardController extends AbstractController {

    private final LivreService livreService;

    public LivreDashboardController(LivreService livreService) {
        this.livreService = livreService;
    }


    @GetMapping({"", "/"})
    public String listeLivres(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("titre").ascending());
        
        Page<Livre> livresPage;
        if (search != null && !search.trim().isEmpty()) {
            livresPage = livreService.searchLivres(search, pageable);
        } else {
            livresPage = livreService.getAllLivres(pageable);
        }
        
        model.addAttribute("livres", livresPage.getContent());
        model.addAttribute("page", livresPage);

        return renderView(model, "dashboard/livres/liste_livres", "Gestion des livres");
    }


    @GetMapping("/creer")
    public String afficherFormulaireCreationLivre(Model model) {
        model.addAttribute("livre", new Livre());
        return renderView(model, "dashboard/livres/creer_livre", "Créer un livre");
    }


    @PostMapping("/creer")
    public String creerLivre(
            @ModelAttribute @Valid Livre livre,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.livre", "Le titre du livre ne peut pas être vide");
        }

        if (result.hasErrors()) {
            return renderView(model, "dashboard/livres/creer_livre", "Créer un livre");
        }

        livreService.createLivre(livre);
        addSuccessMessage(redirectAttributes, "Livre créée avec succès");
        return "redirect:/dashboard/livres";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationLivre(@PathVariable() Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        
        if (livreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Livre non trouvé");
            return "redirect:/dashboard/livres";
        }
        
        model.addAttribute("livre", livreOpt.get());
        return renderView(model, "dashboard/livres/modifier_livre", "Modifier le livre");
    }
    
    @PostMapping("/{id}/modifier")
    public String modifierLivre(
            @PathVariable() Long id,
            @ModelAttribute @Valid Livre livre,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<Livre> livreExistanteOpt = livreService.getLivreById(id);
        
        if (livreExistanteOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Livre non trouvé");
            return "redirect:/dashboard/livres";
        }
        
        // Vérifier que le titre n'est pas vide
        if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.livre", "Le titre dulivre ne peut pas être vide");
        }
        
        if (result.hasErrors()) {
            return renderView(model, "dashboard/livres/modifier_livre", "Modifier le livre");
        }
        
        livre.setId(id);
        livreService.updateLivre(livre);
        
        addSuccessMessage(redirectAttributes, "Livre modifiée avec succès");
        return "redirect:/dashboard/livres";
    }

    @GetMapping("/{id}/supprimer")
    public String afficherFormulaireSuppressionLivre(@PathVariable() Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        
        if (livreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Livre non trouvé");
            return "redirect:/dashboard/livres";
        }
        
        model.addAttribute("livre", livreOpt.get());
        return renderView(model, "dashboard/livres/supprimer_livre", "Supprimer le livre");
    }
    
    @PostMapping("/{id}/supprimer")
    public String supprimerLivre(@PathVariable() Long id, RedirectAttributes redirectAttributes) {
        Optional<Livre> livreOpt = livreService.getLivreById(id);
        
        if (livreOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Livre non trouvé");
            return "redirect:/dashboard/livres";
        }
        
        try {
            livreService.deleteLivre(id);
            addSuccessMessage(redirectAttributes, "Livre supprimée avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer cet livre. il est peut-être assigné a des livres.");
        }
        
        return "redirect:/dashboard/livres";
    }

}