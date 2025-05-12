package uit.fs.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

}