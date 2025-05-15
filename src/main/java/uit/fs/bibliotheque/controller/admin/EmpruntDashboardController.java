package uit.fs.bibliotheque.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uit.fs.bibliotheque.model.ConfigurationEmprunt;
import uit.fs.bibliotheque.model.EmpruntLivre;
import uit.fs.bibliotheque.model.EmpruntLivre.StatutEmprunt;
import uit.fs.bibliotheque.repository.ConfigurationEmpruntRepository;
import uit.fs.bibliotheque.repository.EmpruntLivreRepository;
@Controller
@RequestMapping("/dashboard/emprunts")
public class EmpruntDashboardController {

    
    @Autowired
    private ConfigurationEmpruntRepository configRepository;

    @Autowired
    private EmpruntLivreRepository empruntRepository;
    

    @GetMapping("")
    public String adminTousEmprunts(
            Model model,
            @RequestParam(name = "statut", required = false) String statut,
            @RequestParam(name = "utilisateurId", required = false) Long utilisateurId,
            @RequestParam(name = "livreId", required = false) Long livreId,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "dateEmprunt"));
        Page<EmpruntLivre> emprunts;
        
        // Application des filtres
        if (statut != null && !statut.isEmpty() && utilisateurId != null && livreId != null) {
            emprunts = empruntRepository.findByStatutAndUtilisateurIdAndLivreId(
                StatutEmprunt.valueOf(statut.toUpperCase()), 
                utilisateurId, 
                livreId, 
                pageable
            );
        } else if (statut != null && !statut.isEmpty() && utilisateurId != null) {
            emprunts = empruntRepository.findByStatutAndUtilisateurId(
                StatutEmprunt.valueOf(statut.toUpperCase()), 
                utilisateurId, 
                pageable
            );
        } else if (statut != null && !statut.isEmpty() && livreId != null) {
            emprunts = empruntRepository.findByStatutAndLivreId(
                StatutEmprunt.valueOf(statut.toUpperCase()), 
                livreId, 
                pageable
            );
        } else if (utilisateurId != null && livreId != null) {
            emprunts = empruntRepository.findByUtilisateurIdAndLivreId(
                utilisateurId, 
                livreId, 
                pageable
            );
        } else if (statut != null && !statut.isEmpty()) {
            emprunts = empruntRepository.findByStatut(
                StatutEmprunt.valueOf(statut.toUpperCase()), 
                pageable
            );
        } else if (utilisateurId != null) {
            emprunts = empruntRepository.findByUtilisateurId(utilisateurId, pageable);
        } else if (livreId != null) {
            emprunts = empruntRepository.findByLivreId(livreId, pageable);
        } else {
            emprunts = empruntRepository.findAll(pageable);
        }
        
        model.addAttribute("emprunts", emprunts);
        model.addAttribute("statutActif", statut);
        model.addAttribute("utilisateurId", utilisateurId);
        model.addAttribute("livreId", livreId);
        
        return "dashboard/emprunts/liste_emprunts";
    }
    
    @GetMapping("/configuration")
    public String adminConfigurationEmpruntForm(Model model) {
        ConfigurationEmprunt config = getConfiguration();
        model.addAttribute("config", config);
        
        return "dashboard/emprunts/configuration";
    }
    
    @PostMapping("/configuration")
    public String adminConfigurationEmpruntSubmit(ConfigurationEmprunt config, RedirectAttributes redirectAttributes) {
        configRepository.save(config);
        redirectAttributes.addFlashAttribute("success", "La configuration d'emprunt a été mise à jour.");
        
        return "redirect:/dashboard/emprunts/configuration";
    }
    
    
    private ConfigurationEmprunt getConfiguration() {
        return configRepository.findAll()
            .stream()
            .findFirst()
            .orElseGet(() -> {
                ConfigurationEmprunt newConfig = new ConfigurationEmprunt();
                return configRepository.save(newConfig);
            });
    }
}
