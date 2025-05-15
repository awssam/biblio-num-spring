package uit.fs.bibliotheque.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uit.fs.bibliotheque.model.ConfigurationEmprunt;
import uit.fs.bibliotheque.model.EmpruntLivre;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.model.Utilisateur;
import uit.fs.bibliotheque.repository.ConfigurationEmpruntRepository;
import uit.fs.bibliotheque.repository.EmpruntLivreRepository;

@Service
@Transactional
public class EmpruntService {

    @Autowired
    private EmpruntLivreRepository empruntRepository;
    
    @Autowired
    private ConfigurationEmpruntRepository configRepository;

    public Map<String, Object> peutEmprunter(Utilisateur utilisateur, Livre livre) {
        Map<String, Object> result = new HashMap<>();
        result.put("peutEmprunter", true);
        result.put("message", "");

        ConfigurationEmprunt config = getConfiguration();
        
        // Vérifier si l'utilisateur a atteint sa limite d'emprunts
        long empruntsActifs = empruntRepository.countByUtilisateurAndStatut(
            utilisateur, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
        
        if (empruntsActifs >= config.getMaxEmpruntsUtilisateur()) {
            result.put("peutEmprunter", false);
            result.put("message", "Vous avez atteint votre limite d'emprunts simultanés.");
            return result;
        }
        
        // Vérifier si des copies du livre sont disponibles
        long empruntsLivreActifs = empruntRepository.countByLivreAndStatut(
            livre, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
        
        if (livre.getCopiesDisponibles() <= empruntsLivreActifs) {
            result.put("peutEmprunter", false);
            result.put("message", "Aucune copie de ce livre n'est disponible pour l'emprunt.");
            return result;
        }
        
        // Vérifier si l'utilisateur a déjà emprunté ce livre
        boolean empruntExistant = empruntRepository.existsByUtilisateurAndLivreAndStatut(
            utilisateur, 
            livre, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
        
        if (empruntExistant) {
            result.put("peutEmprunter", false);
            result.put("message", "Vous avez déjà emprunté ce livre.");
            return result;
        }
        
        return result;
    }
    
    public EmpruntLivre emprunterLivre(Utilisateur utilisateur, Livre livre, Integer dureeEmprunt) {
        ConfigurationEmprunt config = getConfiguration();
        if (dureeEmprunt == null || dureeEmprunt < 1 || dureeEmprunt > config.getDureeEmpruntDefaut()) {
            throw new IllegalArgumentException("La durée d'emprunt doit être entre 1 et " + config.getDureeEmpruntDefaut());
        }
        Map<String, Object> verification = peutEmprunter(utilisateur, livre);
        
        if (!(Boolean) verification.get("peutEmprunter")) {
            throw new IllegalStateException((String) verification.get("message"));
        }
        
        
        EmpruntLivre emprunt = EmpruntLivre.builder()
            .utilisateur(utilisateur)
            .livre(livre)
            .dureeEmprunt(config.getDureeEmpruntDefaut())
            .statut(EmpruntLivre.StatutEmprunt.EN_COURS)
            .build();
        
        return empruntRepository.save(emprunt);
    }
    
    public boolean renouvelerEmprunt(Long empruntId) {
        EmpruntLivre emprunt = empruntRepository.findById(empruntId)
            .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));
            
        ConfigurationEmprunt config = getConfiguration();
        
        return false;
    }
    
    public boolean retournerLivre(Long empruntId) {
        EmpruntLivre emprunt = empruntRepository.findById(empruntId)
            .orElseThrow(() -> new IllegalArgumentException("Emprunt non trouvé"));
            
        if (emprunt.retourner()) {
            empruntRepository.save(emprunt);
            return true;
        }
        
        return false;
    }
    
    public ConfigurationEmprunt getConfiguration() {
        return configRepository.findAll()
            .stream()
            .findFirst()
            .orElseGet(() -> {
                ConfigurationEmprunt newConfig = new ConfigurationEmprunt();
                return configRepository.save(newConfig);
            });
    }

    public long getNombreCopiesEmpruntees(Livre livre) {
        return empruntRepository.countByLivreAndStatut(
            livre, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
    }
    
    public long getNombreEmpruntsUtilisateur(Utilisateur utilisateur) {
        return empruntRepository.countByUtilisateurAndStatut(
            utilisateur, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
    }
    
    public boolean livreActuelmentEmprunteParUtilisateur(Livre livre, Utilisateur utilisateur) {
        return empruntRepository.existsByUtilisateurAndLivreAndStatut(
            utilisateur, 
            livre, 
            EmpruntLivre.StatutEmprunt.EN_COURS
        );
    }
}
