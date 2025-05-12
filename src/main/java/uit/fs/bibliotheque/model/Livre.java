package uit.fs.bibliotheque.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "livres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"auteurs"})
@ToString(exclude = {"auteurs"})
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(name = "couverture")
    private String couverture;

    @Column(columnDefinition = "TEXT")
    private String resume;

    @Column(name = "copies_disponibles")
    private Integer copiesDisponibles = 1;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(name = "date_ajout", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateAjout;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "livre_auteur",
        joinColumns = @JoinColumn(name = "livre_id"),
        inverseJoinColumns = @JoinColumn(name = "auteur_id")
    )
    private Set<Auteur> auteurs = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
    }

    // Constructeur avec titre, resume et couverture
    public Livre(String titre, String resume, String couverture) {
        this.titre = titre;
        this.resume = resume;
        this.couverture = couverture;
    }

    // Méthode pour obtenir le nom complet
    public String getTitreAvecAuteurs() {
        if (titre != null && !titre.isEmpty()) {
            StringBuilder titreComplet = new StringBuilder(titre);
            titreComplet.append(" par: ");
            if (!auteurs.isEmpty()) {
                auteurs.forEach(auteur -> 
                    titreComplet.append(auteur.getNomComplet()).append(", "));
                // Enlever la dernière virgule et espace
                titreComplet.setLength(titreComplet.length() - 2);
            } else {
                titreComplet.append("Auteur inconnu");
            }
            return titreComplet.toString();
        }
        return "Titre non disponible";
    }

    public String getBeautifulAuteurs() {
        if (titre != null && !titre.isEmpty()) {
            StringBuilder titreComplet = new StringBuilder("");
            if (!auteurs.isEmpty()) {
                if (auteurs.size() >= 2) {
                    // Prendre les deux premiers auteurs
                    Auteur[] deuxAuteurs = auteurs.stream().limit(2).toArray(Auteur[]::new);
                    String auteur1 = formatAuteurInitiales(deuxAuteurs[0], false);
                    String auteur2 = formatAuteurInitiales(deuxAuteurs[1], false);
                    titreComplet.append(auteur1).append(" et ").append(auteur2);
                    
                    // S'il y a plus de deux auteurs
                    int autresAuteurs = auteurs.size() - 2;
                    if (autresAuteurs > 0) {
                        titreComplet.append(" et ").append(autresAuteurs)
                                  .append(autresAuteurs == 1 ? " autre" : " autres");
                    }
                } else {
                    // Un seul auteur - afficher le nom complet
                    Auteur premierAuteur = auteurs.iterator().next();
                    titreComplet.append(formatAuteurInitiales(premierAuteur, true));
                }
            } else {
                titreComplet.append("Auteur inconnu");
            }
            return titreComplet.toString();
        }
        return "Titre non disponible";
    }

    // Méthode pour formater le nom d'un auteur avec initiales
    private String formatAuteurInitiales(Auteur auteur, boolean nomComplet) {
        String nom = auteur.getNom();
        String prenom = auteur.getPrenom();
        
        if (nom != null && !nom.isEmpty() && prenom != null && !prenom.isEmpty()) {
            if (nomComplet) {
                return prenom + " " + nom;
            } else {
                return prenom.charAt(0) + "." + nom;
            }
        } else if (nom != null && !nom.isEmpty()) {
            return nom;
        } else if (prenom != null && !prenom.isEmpty()) {
            return prenom;
        }
        return "Inconnu";
    }

    // Méthode pour obtenir le nom de la couverture
    public String getCouvertureURL() {
        if (couverture != null && !couverture.isEmpty()) {
            return couverture;
        }
        return "Couverture non disponible";
    }

    // Méthodes utilitaires pour gérer la relation avec les auteurs
    public void addAuteur(Auteur auteur) {
        this.auteurs.add(auteur);
        auteur.getLivres().add(this);
    }

    public void removeAuteur(Auteur auteur) {
        this.auteurs.remove(auteur);
        auteur.getLivres().remove(this);
    }
}