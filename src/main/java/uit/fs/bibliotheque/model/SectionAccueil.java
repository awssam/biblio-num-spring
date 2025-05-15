package uit.fs.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sections_accueil")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionAccueil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "nombre_livres", nullable = false)
    private Integer nombreLivres;

    @Column(name = "livres_par_page", nullable = false)
    private Integer livresParPage;

    @Column(name = "intervalle_defilement", nullable = false)
    private Integer intervalleDefilement;

    @Column(name = "disponibles_uniquement")
    private Boolean disponiblesUniquement;
    
    // Constructeur avec les paramètres principaux
    public SectionAccueil(String titre, Categorie categorie, Integer ordre, Integer nombreLivres, 
                        Integer livresParPage, Integer intervalleDefilement, Boolean disponiblesUniquement) {
        this.titre = titre;
        this.categorie = categorie;
        this.ordre = ordre;
        this.nombreLivres = nombreLivres;
        this.livresParPage = livresParPage;
        this.intervalleDefilement = intervalleDefilement;
        this.disponiblesUniquement = disponiblesUniquement;
    }
}
