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
@Table(name = "auteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column
    private String prenom;

    @Column(columnDefinition = "TEXT")
    private String biographie;


    // Constructeur avec nom et prénom
    public Auteur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    // Méthode pour obtenir le nom complet
    public String getNomComplet() {
        if (prenom != null && !prenom.isEmpty()) {
            return prenom + " " + nom;
        }
        return nom;
    }
}