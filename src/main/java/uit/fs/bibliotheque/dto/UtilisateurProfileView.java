package uit.fs.bibliotheque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import uit.fs.bibliotheque.model.Utilisateur;

@Data
@AllArgsConstructor
public class UtilisateurProfileView {
    private Utilisateur utilisateur;
    private String profileImageUrl;
}
