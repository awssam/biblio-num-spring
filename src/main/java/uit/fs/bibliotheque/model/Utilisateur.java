package uit.fs.bibliotheque.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    

    @Column(nullable = false)
    private String role;

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false)
    private LocalDateTime dateCreation;


    // for django
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "is_superuser")
    @Builder.Default
    private boolean isSuperuser = false;

    @Column(name = "is_staff")
    @Builder.Default
    private boolean isStaff = false;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "account_non_expired" , nullable = true)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    // Méthodes de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }
    
    public boolean isAdmin() {
        return RoleConstants.ADMINISTRATEUR.equalsIgnoreCase(role);
    }

    public boolean isMember() {
        return RoleConstants.MEMBRE.equalsIgnoreCase(role);
    }
}