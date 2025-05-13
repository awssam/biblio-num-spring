package uit.fs.bibliotheque.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.service.DashbordService;

@Controller
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('MEMBRE') or hasRole('ADMINISTRATEUR')")
/**
 * Contrôleur pour la gestion des auteurs dans le tableau de bord.
 */
public class DashboardAccueilController extends AbstractController {

    private final DashbordService dashboardService;


    public DashboardAccueilController(DashbordService dashboardService) {
        this.dashboardService = dashboardService;
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

        // Ajouter les informations au modèle
        model.addAttribute("countOfAllAuthors", countOfAllAuthors);
        model.addAttribute("countOfAllBooks", countOfAllBooks);
        model.addAttribute("countOfAllCategories", countOfAllCategories);
        model.addAttribute("countOfAllUsers", countOfAllUsers);
        model.addAttribute("recentBooks", recentBooks);
        model.addAttribute("recentAuthors", recentAuthors);

        return renderView(model, "dashboard/accueil", "Dashboard Accueil");
    }

}