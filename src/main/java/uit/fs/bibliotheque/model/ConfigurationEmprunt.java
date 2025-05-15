package uit.fs.bibliotheque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuration_emprunt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationEmprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duree_emprunt_defaut")
    @Builder.Default
    private Integer dureeEmpruntDefaut = 14;

    @Column(name = "max_emprunts_utilisateur")
    @Builder.Default
    private Integer maxEmpruntsUtilisateur = 5;

    @Column(name = "duree_emprunt_max")
    @Builder.Default
    private Integer dureeMaxEmprunts = 2;
}
