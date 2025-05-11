package uit.fs.bibliotheque.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uit.fs.bibliotheque.model.Utilisateur;

/**
 * Contrôleur abstrait qui fournit des fonctionnalités communes à tous les contrôleurs
 */
public abstract class AbstractController {

    protected Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        return null;
    }

    protected boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    protected String renderView(Model model, String viewName, String pageTitle) {
        model.addAttribute("pageTitle", pageTitle);
        
        Utilisateur currentUser = getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        
        return viewName;
    }

    protected void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
    }

    protected void addErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }

    protected void addInfoMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("infoMessage", message);
    }
}