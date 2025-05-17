
package uit.fs.bibliotheque.helpers;
import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import uit.fs.bibliotheque.model.Utilisateur;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ("IS_ADMIN".equals(permission)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Utilisateur utilisateur) {
                return utilisateur.isAdmin();
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ("IS_ADMIN".equals(permission)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Utilisateur utilisateur) {
                return utilisateur.isAdmin();
            }
        }
        return false;
    }
}