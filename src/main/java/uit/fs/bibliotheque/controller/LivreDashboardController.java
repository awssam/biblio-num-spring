package uit.fs.bibliotheque.controller;

import java.io.IOException;
import java.time.LocalDate;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.service.ImageService;
import uit.fs.bibliotheque.service.LivreService;

@Controller
@RequestMapping("/dashboard/livres")
@PreAuthorize("hasRole('MEMBRE') or hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des livres dans le tableau de bord.
 */
public class LivreDashboardController extends AbstractController {

    private final LivreService livreService;
    private final ImageService imageService;

    public LivreDashboardController(LivreService livreService, ImageService imageService) {
        this.livreService = livreService;
        this.imageService = imageService;
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
            @ModelAttribute("livre") @Valid Livre livre,
            BindingResult result,
            @RequestParam(name = "fichierCouverture", required = false) MultipartFile fichierCouverture,
            Model model,
            RedirectAttributes redirectAttributes) {
                livre.setCouverture(null);

        if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.livre", "Le titre du livre ne peut pas être vide");
        }

        if (livre.getCopiesDisponibles() == null || livre.getCopiesDisponibles() < 0) {
            result.rejectValue("copiesDisponibles", "error.livre", "Le nombre de copies doit être positif ou nul");
        }

        if (result.hasErrors()) {
            return renderView(model, "dashboard/livres/creer_livre", "Créer un livre");
        }

        try {
            // Sauvegarde de l'image de couverture si fournie
            if (fichierCouverture != null && !fichierCouverture.isEmpty()) {
                String imagePath = imageService.saveImage(fichierCouverture, "livre");
                livre.setCouverture(imagePath);
            }
            
            livreService.createLivre(livre);
            addSuccessMessage(redirectAttributes, "Livre créé avec succès");
            return "redirect:/dashboard/livres";
        } catch (IOException e) {
            result.reject("error.upload", "Erreur lors de l'upload de l'image: " + e.getMessage());
            return renderView(model, "dashboard/livres/creer_livre", "Créer un livre");
        }
    }

    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModificationLivre(
        @PathVariable() Long id, 
        Model model, RedirectAttributes redirectAttributes) {
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
            @RequestParam(name = "fichierCouverture", required = false) MultipartFile fichierCouverture,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Optional<Livre> livreExistanteOpt = livreService.getLivreById(id);

        if (livreExistanteOpt.isEmpty()) {
            addErrorMessage(redirectAttributes, "Livre non trouvé");
            return "redirect:/dashboard/livres";
        }

        // Validation
        if (livre.getTitre() == null || livre.getTitre().isEmpty()) {
            result.rejectValue("titre", "error.livre", "Le titre du livre ne peut pas être vide");
        }
        
        if (livre.getCopiesDisponibles() == null || livre.getCopiesDisponibles() < 0) {
            result.rejectValue("copiesDisponibles", "error.livre", "Le nombre de copies doit être positif ou nul");
        }

        if (livre.getDatePublication() != null && livre.getDatePublication().isAfter(LocalDate.now())) {
            result.rejectValue("datePublication", "error.livre", "La date de publication ne peut pas être dans le futur");
        }
        
        if (result.hasErrors()) {
            return renderView(model, "dashboard/livres/modifier_livre", "Modifier le livre");
        }
        
        try {
            Livre livreExistant = livreExistanteOpt.get();
            
            // Préserver la couverture existante si aucune nouvelle n'est fournie
            if (fichierCouverture == null || fichierCouverture.isEmpty()) {
                livre.setCouverture(livreExistant.getCouverture());
            } else {
                // Supprimer l'ancienne image si elle existe
                if (livreExistant.getCouverture() != null && !livreExistant.getCouverture().isEmpty()) {
                    imageService.deleteImage(livreExistant.getCouverture(), "livre");
                }
                
                // Sauvegarder la nouvelle image
                String imagePath = imageService.saveImage(fichierCouverture, "livre");
                livre.setCouverture(imagePath);
            }
            
            livre.setId(id);
            livreService.updateLivre(livre);
            
            addSuccessMessage(redirectAttributes, "Livre modifié avec succès");
            return "redirect:/dashboard/livres";
        } catch (IOException e) {
            result.reject("error.upload", "Erreur lors de l'upload de l'image: " + e.getMessage());
            return renderView(model, "dashboard/livres/modifier_livre", "Modifier le livre");
        }
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
            Livre livreExistant = livreOpt.get();

            if (livreExistant.getCouverture() != null && !livreExistant.getCouverture().isEmpty()) {
                    imageService.deleteImage(livreExistant.getCouverture(), "livre");
                }
            livreService.deleteLivre(id);
            addSuccessMessage(redirectAttributes, "Livre supprimée avec succès");
        } catch (Exception e) {
            addErrorMessage(redirectAttributes, "Impossible de supprimer cet livre. il est peut-être assigné a des livres.");
        }
        
        return "redirect:/dashboard/livres";
    }

}