package uit.fs.bibliotheque.controller.site;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uit.fs.bibliotheque.controller.AbstractController;
import uit.fs.bibliotheque.dto.EmpruntForm;
import uit.fs.bibliotheque.model.ConfigurationEmprunt;
import uit.fs.bibliotheque.model.EmpruntLivre;
import uit.fs.bibliotheque.model.EmpruntLivre.StatutEmprunt;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.repository.EmpruntLivreRepository;
import uit.fs.bibliotheque.repository.LivreRepository;
import uit.fs.bibliotheque.service.EmpruntService;

@Controller
@RequestMapping("/")
public class EmpruntController extends AbstractController {

    @Autowired
    private EmpruntLivreRepository empruntRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private EmpruntService empruntService;

    @GetMapping("/mon-compte/mes-emprunts")
    public String mesEmprunts(Model model,
            @RequestParam(name = "statut", required = false) String statut,
            @RequestParam(name = "page", defaultValue = "0") int page) {

        Utilisateur utilisateur = (Utilisateur) getCurrentUser();
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,
                "dateEmprunt"));
        Page<EmpruntLivre> emprunts;
        if (statut != null && !statut.isEmpty()) {
            emprunts = empruntRepository.findByUtilisateurAndStatut(
                    utilisateur,
                    StatutEmprunt.valueOf(statut.toUpperCase()),
                    pageable
            );
        } else {
            emprunts = empruntRepository.findByUtilisateur(utilisateur, pageable);
        }
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statutActif", statut);
        return renderView(model, "emprunts/mes_emprunts", "Mes emprunts");
    }

    @GetMapping("/emprunter/{livreId}")
    public String emprunterLivreForm(@PathVariable Long livreId, Model model) {
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new IllegalArgumentException("Livre non trouvé"));

        ConfigurationEmprunt config = empruntService.getConfiguration();

        long empruntsActifs = empruntRepository.countByLivreAndStatut(livre, StatutEmprunt.EN_COURS);
        int exemplairesRestants = livre.getCopiesDisponibles() - (int) empruntsActifs;

        if (exemplairesRestants <= 0) {
            return "redirect:/livres/" + livre.getId() + "?error=non_disponible";
        }

        EmpruntForm empruntForm = new EmpruntForm();
        empruntForm.setDureeEmprunt(config.getDureeEmpruntDefaut());

        model.addAttribute("livre", livre);
        model.addAttribute("empruntForm", empruntForm);
        model.addAttribute("dureeDefaut", config.getDureeEmpruntDefaut());
        model.addAttribute("dureeMax", config.getDureeEmpruntDefaut());
        model.addAttribute("exemplairesRestants", exemplairesRestants);

        return renderView(model, "emprunts/emprunter_livre", "Emprunter le livre");
    }

    @PostMapping("/emprunter/{livreId}")
    public String emprunterLivre(
            @PathVariable Long livreId,
            @RequestParam("dureeEmprunt") Integer dureeEmprunt,
            RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) getCurrentUser();
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new IllegalArgumentException("Livre non trouvé"));

        try {
            empruntService.emprunterLivre(utilisateur, livre, dureeEmprunt);
            addSuccessMessage(redirectAttributes,
                "Vous avez emprunté '" + livre.getTitre() + "' pour " + dureeEmprunt + " jours.");
            return "redirect:/mon-compte/mes-emprunts";

        } catch (IllegalArgumentException | IllegalStateException e) {
            addErrorMessage(redirectAttributes, e.getMessage());
            return "redirect:/livres/" + livre.getId();
        }

    }

    @PostMapping("/mon-compte/mes-emprunts/{empruntId}/retourner")
    public String retournerLivre(
            @PathVariable Long empruntId,
            RedirectAttributes redirectAttributes) {

        Utilisateur utilisateur = (Utilisateur) getCurrentUser();
        EmpruntLivre emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));

        // Vérifier que l'emprunt appartient à l'utilisateur connecté
        if (!emprunt.getUtilisateur().getId().equals(utilisateur.getId())) {
            addErrorMessage(redirectAttributes, "Vous n'êtes pas autorisé à effectuer cette action.");
            return "redirect:/mon-compte/mes-emprunts";
        }

        if (emprunt.retourner()) {
            empruntRepository.save(emprunt);
            addSuccessMessage(redirectAttributes, "Vous avez retourné '" + emprunt.getLivre().getTitre() + "'.");
        } else {
            addErrorMessage(redirectAttributes, "Ce livre a déjà été retourné ou l'emprunt a été annulé.");
        }

        return "redirect:/mon-compte/mes-emprunts";
    }

    // @GetMapping("/mon-compte/mes-emprunts/{empruntId}")
    // public String empruntDetail(
    //         @PathVariable Long empruntId,
    //         Model model,
    //         RedirectAttributes redirectAttributes) {
    //     Utilisateur utilisateur = (Utilisateur) getCurrentUser();
    //     Optional<EmpruntLivre> optionalEmprunt = empruntRepository.findById(empruntId);
    //     if (optionalEmprunt.isEmpty()) {
    //         addErrorMessage(redirectAttributes, "Emprunt non trouvé.");
    //         return "redirect:/mon-compte/mes-emprunts";
    //     }
    //     EmpruntLivre emprunt = optionalEmprunt.get();
    //     // Vérifier que l'emprunt appartient à l'utilisateur connecté
    //     if (!emprunt.getUtilisateur().getId().equals(utilisateur.getId()) && !utilisateur.getRole().equals("ADMIN")) {
    //         addErrorMessage(redirectAttributes, "Vous n'êtes pas autorisé à accéder à cet emprunt.");
    //         return "redirect:/mon-compte/mes-emprunts";
    //     }
    //     model.addAttribute("emprunt", emprunt);
    //     return "emprunts/emprunt_detail";
    // }
}
