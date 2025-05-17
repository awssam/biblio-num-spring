package uit.fs.bibliotheque.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uit.fs.bibliotheque.controller.AbstractController;
import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.SectionAccueil;
import uit.fs.bibliotheque.model.SliderLivre;
import uit.fs.bibliotheque.service.AccueilService;
import uit.fs.bibliotheque.service.DashbordService;

@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasAuthority('IS_ADMIN')")
/**
 * Contrôleur pour la gestion des auteurs dans le tableau de bord.
 */
public class DashboardAccueilController extends AbstractController {

    private final DashbordService dashboardService;
    private final AccueilService accueilService;

    public DashboardAccueilController(DashbordService dashboardService, AccueilService accueilService) {
        this.dashboardService = dashboardService;
        this.accueilService = accueilService;
    }


    @GetMapping({"", "/"})
    public String accueil(Model model) {
        // Récupérer les informations nécessaires pour le tableau de bord
        Long countOfAllAuthors = dashboardService.getCountOfAllAuthors();
        Long countOfAllBooks = dashboardService.getCountOfAllBooks();
        Long countOfAllCategories = dashboardService.getCountOfAllCategories();
        Long countOfAllUsers = dashboardService.getCountOfAllUsers();
        
        // Récupérer les livres et auteurs récents
        List<Livre> recentBooks = dashboardService.getRecentBooks();
        List<Auteur> recentAuthors = dashboardService.getRecentAuthors();
        
        // Récupérer les informations de gestion de la page d'accueil
        List<SliderLivre> slidersLivres = accueilService.getAllSliderLivres();
        List<SectionAccueil> sectionsAccueil = accueilService.getAllSectionsAccueil();

        // Ajouter les informations au modèle
        model.addAttribute("countOfAllAuthors", countOfAllAuthors);
        model.addAttribute("countOfAllBooks", countOfAllBooks);
        model.addAttribute("countOfAllCategories", countOfAllCategories);
        model.addAttribute("countOfAllUsers", countOfAllUsers);
        model.addAttribute("recentBooks", recentBooks);
        model.addAttribute("recentAuthors", recentAuthors);
        model.addAttribute("slidersLivres", slidersLivres);
        model.addAttribute("sectionsAccueil", sectionsAccueil);

        return renderView(model, "dashboard/accueil", "Dashboard Accueil");
    }
    
   
    @GetMapping("/recent-livres")
    @ResponseBody
    public ResponseEntity<List<Livre>> getRecentBooks() {
        List<Livre> recentBooks = dashboardService.getRecentBooks();
        return ResponseEntity.ok(recentBooks);
    }
    
    @GetMapping("/recent-auteurs")
    @ResponseBody
    public ResponseEntity<List<Auteur>> getRecentAuthors() {
        List<Auteur> recentAuthors = dashboardService.getRecentAuthors();
        return ResponseEntity.ok(recentAuthors);
    }

}