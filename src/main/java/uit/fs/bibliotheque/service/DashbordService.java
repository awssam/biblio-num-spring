package uit.fs.bibliotheque.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import uit.fs.bibliotheque.model.Auteur;
import uit.fs.bibliotheque.model.Livre;
import uit.fs.bibliotheque.repository.AuteurRepository;
import uit.fs.bibliotheque.repository.CategorieRepository;
import uit.fs.bibliotheque.repository.LivreRepository;
import uit.fs.bibliotheque.repository.UtilisateurRepository;

@Service
public class DashbordService {

    private final AuteurRepository auteurRepository;
    private final LivreRepository livreRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;

    public DashbordService(AuteurRepository auteurRepository
            , LivreRepository livreRepository, CategorieRepository categorieRepository,
            UtilisateurRepository utilisateurRepository) {
        this.auteurRepository = auteurRepository;
        this.livreRepository = livreRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // get count of all authors , books, categories and users
    public Long getCountOfAllAuthors() {
        return auteurRepository.count();
    }
    public Long getCountOfAllBooks() {
        return livreRepository.count();
    }
    public Long getCountOfAllCategories() {
        return categorieRepository.count();
    }
    public Long getCountOfAllUsers() {
        return utilisateurRepository.count();
    }

    // Récupérer les 5 derniers livres ajoutés
    public List<Livre> getRecentBooks() {
        return livreRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dateAjout"))
        ).getContent();
    }

    // Récupérer les 5 derniers auteurs ajoutés
    public List<Auteur> getRecentAuthors() {
        return auteurRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "prenom"))
        ).getContent();
    }
}
