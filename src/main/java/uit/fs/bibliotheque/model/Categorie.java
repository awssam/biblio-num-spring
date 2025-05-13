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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"livres"})
@ToString(exclude = {"livres"})
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Le nom de la catégorie ne peut pas être vide")
    @Size(min = 3, message = "Le nom de la catégorie doit contenir au moins 3 caractères")
    private String nom;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<Livre> livres = new HashSet<>();

    public Categorie(String nom) {
        this.nom = nom;
    }

    // Méthodes utilitaires pour gérer la relation avec les livres
    public void addLivre(Livre livre) {
        this.livres.add(livre);
        livre.getCategories().add(this);
    }

    public void removeLivre(Livre livre) {
        this.livres.remove(livre);
        livre.getCategories().remove(this);
    }
}