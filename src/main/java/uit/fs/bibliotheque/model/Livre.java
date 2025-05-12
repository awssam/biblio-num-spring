package uit.fs.bibliotheque.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "livres")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
    }

    // Constructeur avec titre , resume et couverture
    public Livre(String titre, String resume, String couverture) {
        this.titre = titre;
        this.resume = resume;
        this.couverture = couverture;
    }

    // Méthode pour obtenir le nom complet
    public String getTitreAvecAuteurs() {
        if (titre != null && !titre.isEmpty()) {
            return titre + " par: Awssam";
        }
        return "Titre non disponible";
    }

    // Méthode pour obtenir le nom de la couverture
    public String getCouvertureURL() {
        if (couverture != null && !couverture.isEmpty()) {
            return couverture;
        }
        return "Couverture non disponible";
    }

}