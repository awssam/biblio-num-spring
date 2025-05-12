package uit.fs.bibliotheque.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "auteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"livres"})
@ToString(exclude = {"livres"})
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

    @JsonIgnore
    @ManyToMany(mappedBy = "auteurs", fetch = FetchType.LAZY)
    private Set<Livre> livres = new HashSet<>();

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

    // Méthodes utilitaires pour gérer la relation avec les livres
    public void addLivre(Livre livre) {
        this.livres.add(livre);
        livre.getAuteurs().add(this);
    }

    public void removeLivre(Livre livre) {
        this.livres.remove(livre);
        livre.getAuteurs().remove(this);
    }
}