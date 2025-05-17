package uit.fs.bibliotheque.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.service.UtilisateurService;

@Controller
public class UtilisateurController extends AbstractController {

    private final UtilisateurService utilisateurService;

    // Constantes pour la validation du mot de passe
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    public UtilisateurController(
            UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/inscription")
    public String afficherPageInscription(Model model) {
        if (isUserLoggedIn()) {
            return "redirect:/";
        }
        
        model.addAttribute("utilisateur", Utilisateur.builder().build());
        
        return renderView(model, "utilisateur/inscription", "Inscription");
    }

    @PostMapping("/inscription")
    public String inscrireUtilisateur(
            @ModelAttribute @Valid Utilisateur utilisateur,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (isUserLoggedIn()) {
            return "redirect:/";
        }

        if (utilisateurService.existsByUsername(utilisateur.getUsername())) {
            result.rejectValue("username", "error.utilisateur", "Ce nom d'utilisateur est déjà utilisé");
        }

        if (utilisateurService.existsByEmail(utilisateur.getEmail())) {
            result.rejectValue("email", "error.utilisateur", "Cet email est déjà utilisé");
        }

        if (result.hasErrors()) { 
            return renderView(model, "utilisateur/inscription", "Inscription");
        }

        // utilisateur.setRole(RoleConstants.MEMBRE);
        
        utilisateurService.createUtilisateur(utilisateur);
        
        addSuccessMessage(redirectAttributes, "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");
        return "redirect:/connexion";
    }

    @GetMapping("/connexion")
    public String afficherPageConnexion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        
        return renderView(model, "utilisateur/connexion", "Connexion");
    }

    @GetMapping("/mon-compte")
    public String afficherMonCompte(Model model) {
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/connexion";
        }
        
        model.addAttribute("utilisateur", currentUser);
        
        return renderView(model, "utilisateur/mon-compte", "Mon compte");
    }

    @GetMapping("/changer-mot-de-passe")
    public String afficherChangerMotDePasse(Model model) {
        if (!isUserLoggedIn()) {
            return "redirect:/connexion";
        }
        
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        
        return renderView(model, "utilisateur/changer-mot-de-passe", "Changer le mot de passe");
    }

    @PostMapping("/changer-mot-de-passe")
    public String changerMotDePasse(
            @ModelAttribute @Valid PasswordChangeRequest passwordChangeRequest,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        Utilisateur currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/connexion";
        }

        if (!utilisateurService.checkPassword(currentUser, passwordChangeRequest.getCurrentPassword())) {
            result.rejectValue("currentPassword", "error.currentPassword", "Le mot de passe actuel est incorrect");
        }

        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Les mots de passe ne correspondent pas");
        }

        // Validation supplémentaire de force du mot de passe
        if (!result.hasFieldErrors("newPassword")) {
            String password = passwordChangeRequest.getNewPassword();
            
            // Vérification manuelle de la complexité si nécessaire
            if (password.length() < MIN_PASSWORD_LENGTH) {
                result.rejectValue("newPassword", "error.password.length", 
                    "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères");
            } else if (!containsUpperCase(password)) {
                result.rejectValue("newPassword", "error.password.uppercase", 
                    "Le mot de passe doit contenir au moins une lettre majuscule");
            } else if (!containsLowerCase(password)) {
                result.rejectValue("newPassword", "error.password.lowercase", 
                    "Le mot de passe doit contenir au moins une lettre minuscule");
            } else if (!containsDigit(password)) {
                result.rejectValue("newPassword", "error.password.digit", 
                    "Le mot de passe doit contenir au moins un chiffre");
            } else if (!containsSpecialChar(password)) {
                result.rejectValue("newPassword", "error.password.special", 
                    "Le mot de passe doit contenir au moins un caractère spécial (@#$%^&+=!)");
            }
        }

        if (result.hasErrors()) {
            return renderView(model, "utilisateur/changer-mot-de-passe", "Changer le mot de passe");
        }

        utilisateurService.changePassword(currentUser, passwordChangeRequest.getNewPassword());
        
        addSuccessMessage(redirectAttributes, "Votre mot de passe a été changé avec succès");
        return "redirect:/mon-compte";
    }
    
    // Méthodes utilitaires pour la validation du mot de passe
    private boolean containsUpperCase(String str) {
        return str.chars().anyMatch(Character::isUpperCase);
    }
    
    private boolean containsLowerCase(String str) {
        return str.chars().anyMatch(Character::isLowerCase);
    }
    
    private boolean containsDigit(String str) {
        return str.chars().anyMatch(Character::isDigit);
    }
    
    private boolean containsSpecialChar(String str) {
        String specialChars = "@#$%^&+=!";
        return str.chars().anyMatch(ch -> specialChars.indexOf(ch) >= 0);
    }

    @Data
    public static class PasswordChangeRequest {
        @NotBlank(message = "Le mot de passe actuel est obligatoire")
        private String currentPassword;
        
        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        @Size(min = MIN_PASSWORD_LENGTH, message = "Le mot de passe doit contenir au moins {min} caractères")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", 
            message = "Le mot de passe doit contenir au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial (@#$%^&+=!)"
        )
        private String newPassword;
        
        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        private String confirmPassword;
    }
}