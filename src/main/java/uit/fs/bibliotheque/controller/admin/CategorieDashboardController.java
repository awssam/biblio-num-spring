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
import uit.fs.bibliotheque.model.Categorie;
import uit.fs.bibliotheque.service.CategorieService;

@Controller
@RequestMapping("/dashboard/categories")
@PreAuthorize("hasAuthority('IS_ADMIN')")
/**
 * Contrôleur pour la gestion des catégories
 */
public class CategorieDashboardController extends AbstractController {

    private final CategorieService categorieService;

    public CategorieDashboardController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }


    @GetMapping({"", "/"})
    public String listeCategories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        
        Page<Categorie> categoriesPage;
        if (search != null && !search.trim().isEmpty()) {
            categoriesPage = categorieService.searchCategories(search, pageable);
        } else {
            categoriesPage = categorieService.getAllCategories(pageable);
        }
        
        model.addAttribute("categories", categoriesPage.getContent());
        model.addAttribute("page", categoriesPage);

        return renderView(model, "dashboard/categories/liste_categories", "Gestion des catégories");
    }


    @GetMapping("/creer")
    public String afficherFormulaireCreationCategorie(Model model) {
        model.addAttribute("categorie", new Categorie());
        return renderView(model, "dashboard/categories/creer_categorie", "Créer une catégorie");
    }


    @PostMapping("/creer")
    public String creerCategorie(
            @ModelAttribute @Valid Categorie categorie,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (categorie.getNom() == null || categorie.getNom().isEmpty()) {
            result.rejectValue("nom", "error.categorie", "Le nom de la catégorie ne peut pas être vide");
        }
        if (categorieService.existsByNom(categorie.getNom())) {
            result.rejectValue("nom", "error.categorie", "Une catégorie avec ce nom existe déjà");
        }

        if (result.hasErrors()) {
            return renderView(model, "dashboard/categories/creer_categorie", "Créer une catégorie");
        }

        categorieService.createCategorie(categorie);
        addSuccessMessage(redirectAttributes, "Catégorie créée avec succès");
        return "redirect:/dashboard/categories";
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationCategorie(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Categorie> categorieOpt = categorieService.getCategorieById(id);
        
        if (categorieOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Catégorie non trouvée");
            return "redirect:/dashboard/categories";
        }
        
        model.addAttribute("categorie", categorieOpt.get());
        return renderView(model, "dashboard/categories/modifier_categorie", "Modifier une catégorie");
    }
    
    @PostMapping("/{id}/modifier")
    public String modifierCategorie(
            @PathVariable() Long id,
            @ModelAttribute @Valid Categorie categorie,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<Categorie> categorieExistanteOpt = categorieService.getCategorieById(id);
        
        if (categorieExistanteOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Catégorie non trouvée");
            return "redirect:/dashboard/categories";
        }
        
        // Vérifier que le nom n'est pas vide
        if (categorie.getNom() == null || categorie.getNom().isEmpty()) {
            result.rejectValue("nom", "error.categorie", "Le nom de la catégorie ne peut pas être vide");
        }
        
        Categorie existingCategorie = categorieExistanteOpt.get();
        if (!existingCategorie.getNom().equalsIgnoreCase(categorie.getNom()) && 
            categorieService.existsByNom(categorie.getNom())) {
            result.rejectValue("nom", "error.categorie", "Une catégorie avec ce nom existe déjà");
        }
        
        if (result.hasErrors()) {
            return renderView(model, "dashboard/categories/modifier_categorie", "Modifier une catégorie");
        }
        
        // Mettre à jour l'ID pour s'assurer que nous modifions la bonne catégorie
        categorie.setId(id);
        categorieService.updateCategorie(categorie);
        
        addSuccessMessage(redirectAttributes, "Catégorie modifiée avec succès");
        return "redirect:/dashboard/categories";
    }

    @GetMapping("/{id}/supprimer")
    public String afficherFormulaireSuppressionCategorie(@PathVariable() Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Categorie> categorieOpt = categorieService.getCategorieById(id);
        
        if (categorieOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Catégorie non trouvée");
            return "redirect:/dashboard/categories";
        }
        
        model.addAttribute("categorie", categorieOpt.get());
        return renderView(model, "dashboard/categories/supprimer_categorie", "Supprimer une catégorie");
    }
    
    @PostMapping("/{id}/supprimer")
    public String supprimerCategorie(@PathVariable() Long id, RedirectAttributes redirectAttributes) {
        Optional<Categorie> categorieOpt = categorieService.getCategorieById(id);
        
        if (categorieOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Catégorie non trouvée");
            return "redirect:/dashboard/categories";
        }
        
        try {
            categorieService.deleteCategorie(id);
            addSuccessMessage(redirectAttributes, "Catégorie supprimée avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer cette catégorie. Elle est peut-être utilisée par des livres.");
        }
        
        return "redirect:/dashboard/categories";
    }

}