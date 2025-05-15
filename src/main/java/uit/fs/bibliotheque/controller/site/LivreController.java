package uit.fs.bibliotheque.controller.site;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uit.fs.bibliotheque.controller.AbstractController;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.repository.EmpruntLivreRepository;
import uit.fs.bibliotheque.service.EmpruntService;
import uit.fs.bibliotheque.service.LivreService;
import uit.fs.bibliotheque.model.ConfigurationEmprunt;
import uit.fs.bibliotheque.model.EmpruntLivre.StatutEmprunt;
import uit.fs.bibliotheque.model.Utilisateur;

/**
 *
 * @author awtsoft
 */
@Controller
@RequestMapping("/livres")
public class LivreController extends AbstractController {
    private final LivreService livreService;
    private final EmpruntService empruntService;
    @Autowired
    private EmpruntLivreRepository empruntRepository;

    
    public LivreController( LivreService livreService, EmpruntService empruntService) {
        this.livreService = livreService;
        this.empruntService = empruntService;
    }


    @GetMapping("/{id}")
    public String listeSlidersLivres(@PathVariable() Long id,Model model) {
        Livre livre = livreService.getLivreById(id).get();
        model.addAttribute("livre", livre);
        return renderView(model, "frontend/livre_detail", livre.getTitre());
    }


    @GetMapping("/{id}/disponibilite_json")
    public ResponseEntity<Object> getDisponibiliteJson(@PathVariable() Long id, Model model) {
        Livre livre = livreService.getLivreById(id).get();
        model.addAttribute("livre", livre);
        
        boolean userAuthenticated = isUserLoggedIn();
        Utilisateur utilisateur = getCurrentUser();
                boolean livreActuelmentEmprunteParUtilisateur = empruntService.livreActuelmentEmprunteParUtilisateur(livre, utilisateur);

        long empruntsActifs = empruntRepository.countByLivreAndStatut(livre, StatutEmprunt.EN_COURS);
        int exemplairesRestants = livre.getCopiesDisponibles() - (int) empruntsActifs;
        
        boolean limiteAtteinte = false;
        String messageLimite = "";

        // ConfigurationEmprunt config = empruntService.getConfiguration();
        if (userAuthenticated) {
            if (empruntsActifs >= exemplairesRestants) {
                limiteAtteinte = true;
                messageLimite = "Vous avez atteint votre limite d'emprunts simultanés.";
            }
        }
        
        if (empruntsActifs >= exemplairesRestants) {
            limiteAtteinte = true;
            messageLimite = "Aucun exemplaire disponible actuellement";
        }
        if(livreActuelmentEmprunteParUtilisateur){
            limiteAtteinte = true;
            messageLimite = "Vous avez déjà emprunté ce livre.";
        }
        
        String datePublication = livre.getDatePublication() != null 
            ? livre.getDatePublication().toString() 
            : "";
        
        Map<String, Object> response = new HashMap<>();
        response.put("livre_id", livre.getId());
        response.put("exemplaires_restants", exemplairesRestants);
        response.put("copies_disponibles", livre.getCopiesDisponibles());
        response.put("user_authenticated", userAuthenticated);
        response.put("limite_atteinte", limiteAtteinte);
        response.put("message_limite", messageLimite);
        response.put("date_publication", datePublication);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/suggestions")
    public ResponseEntity<List<Map<String, Object>>> getLivresSimilaires(@PathVariable Long id) {
        Livre livre = livreService.getLivreById(id)
            .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        
        // Récupérer les livres du même auteur ou même catégorie
        List<Livre> livresSimilaires = livreService.getAllLivres().stream()
            .filter(l -> !l.getId().equals(livre.getId())) // Exclure le livre actuel
            .filter(l -> {
                // Vérifier s'il y a au moins un auteur en commun
                boolean memeAuteur = !l.getAuteurs().stream()
                    .filter(auteur -> livre.getAuteurs().contains(auteur))
                    .collect(Collectors.toSet())
                    .isEmpty();
                
                // Vérifier s'il y a au moins une catégorie en commun
                boolean memeCategorie = !l.getCategories().stream()
                    .filter(categorie -> livre.getCategories().contains(categorie))
                    .collect(Collectors.toSet())
                    .isEmpty();
                
                return memeAuteur || memeCategorie;
            })
            .limit(5) // Limiter à 5 livres similaires
            .collect(Collectors.toList());
        
        List<Map<String, Object>> response = new ArrayList<>();
        
        for (Livre livreSimilaire : livresSimilaires) {
            Map<String, Object> livreData = new HashMap<>();
            
            String auteursDisplay = livreSimilaire.getBeautifulAuteurs();
            Integer exemplairesRestants = livreSimilaire.getCopiesDisponibles();
            
            livreData.put("id", livreSimilaire.getId());
            livreData.put("titre", livreSimilaire.getTitre());
            livreData.put("auteurs_display", auteursDisplay);
            livreData.put("couverture", livreSimilaire.getCouverture() != null ? livreSimilaire.getCouverture() : "");
            livreData.put("copies_disponibles", livreSimilaire.getCopiesDisponibles());
            livreData.put("exemplaires_restants", exemplairesRestants);
            
            response.add(livreData);
        }
        
        return ResponseEntity.ok(response);
    }
}
