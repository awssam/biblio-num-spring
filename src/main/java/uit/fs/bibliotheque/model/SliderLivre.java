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
@Table(name = "slider_livres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SliderLivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;
    
    // Constructeur avec les paramètres principaux
    public SliderLivre(Livre livre, Integer ordre) {
        this.livre = livre;
        this.ordre = ordre;
    }
}
