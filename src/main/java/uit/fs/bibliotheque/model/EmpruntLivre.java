package uit.fs.bibliotheque.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emprunts_livre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpruntLivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Column(name = "date_emprunt", nullable = false, updatable = false)
    private LocalDateTime dateEmprunt;

    @Column(name = "duree_emprunt", nullable = false)
    private Integer dureeEmprunt = 14;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDateTime dateRetourPrevue;

    @Column(name = "date_retour_effective")
    private LocalDateTime dateRetourEffective;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutEmprunt statut = StatutEmprunt.EN_COURS;

    public enum StatutEmprunt {
        EN_COURS("En cours"),
        RENDU("Rendu"),
        RETARD("En retard"),
        ANNULE("Annulé");

        private final String libelle;

        StatutEmprunt(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    @PrePersist
    protected void onCreate() {
        if (dateEmprunt == null) {
            dateEmprunt = LocalDateTime.now();
        }
        if (dateRetourPrevue == null) {
            dateRetourPrevue = dateEmprunt.plusDays(dureeEmprunt);
        }
    }

    public boolean isEnRetard() {
        return statut != StatutEmprunt.RENDU && LocalDateTime.now().isAfter(dateRetourPrevue);
    }

    public long getJoursRestants() {
        if (statut == StatutEmprunt.RENDU) {
            return 0;
        }
        
        long joursRestants = ChronoUnit.DAYS.between(LocalDateTime.now(), dateRetourPrevue);
        return Math.max(0, joursRestants);
    }

    public boolean renouveler(Integer jours) {
        if (statut != StatutEmprunt.EN_COURS) {
            return false;
        }
        
        int joursRenouvellement = jours != null ? jours : 14; // valeur par défaut
        this.dateRetourPrevue = LocalDateTime.now().plusDays(joursRenouvellement);
        return true;
    }

    public boolean retourner() {
        if (statut != StatutEmprunt.RENDU && statut != StatutEmprunt.ANNULE) {
            this.statut = StatutEmprunt.RENDU;
            this.dateRetourEffective = LocalDateTime.now();
            return true;
        }
        return false;
    }

    @Transient
    public static boolean peutEmprunter(Utilisateur utilisateur, Livre livre) {
        // Note: Cette méthode devra être implémentée dans un service
        // car elle nécessite l'accès au repository pour effectuer des requêtes
        return true;
    }

    @Override
    public String toString() {
        return utilisateur.getUsername() + " - " + livre.getTitre();
    }
}
