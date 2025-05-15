# Documentation du Service de Gestion des Fichiers

Ce document présente le fonctionnement du service de gestion des fichiers et images dans l'application Biblio_Num.

## Table des matières
1. [Introduction](#introduction)
2. [Architecture](#architecture)
3. [Configuration](#configuration)
4. [ImageService](#imageservice)
   - [Méthodes principales](#méthodes-principales)
   - [Exemples d'utilisation](#exemples-dutilisation)
5. [ImageUrlConfig](#imageurlconfig)
   - [Fonctionnalités](#fonctionnalités)
   - [Exemples d'utilisation](#exemples-dutilisation-1)
6. [Bonnes pratiques](#bonnes-pratiques)
7. [Cas d'usage courants](#cas-dusage-courants)

## Introduction

Le système de gestion des fichiers de Biblio_Num permet de stocker, récupérer et supprimer des images et autres fichiers. 
Il est principalement utilisé pour gérer les photos de profil des utilisateurs et les couvertures des livres.

## Architecture

Le système de gestion des fichiers est composé de plusieurs composants:

- **ImageService**: Service principal qui gère le stockage physique des fichiers
- **ImageUrlConfig**: Configuration pour la génération des URLs d'images
- **FileStorageConfig**: Configuration Spring pour le stockage des fichiers

L'architecture est conçue pour:
- Séparer le stockage physique des fichiers de leur URL d'accès
- Organiser les fichiers dans des sous-répertoires selon leur usage (utilisateur, livre, etc.)
- Générer des noms de fichiers uniques pour éviter les conflits

## Configuration

La configuration du système de gestion des fichiers est définie dans `application.properties`:

```properties
# Répertoire de stockage des fichiers uploadés
spring.servlet.multipart.location=/chemin/vers/uploads

# URL de base pour accéder aux fichiers uploadés
app.base-upload-url=/uploads/

# URL par défaut pour l'avatar utilisateur
app.default-avatar=/images/default-avatar.png

# Taille maximale des fichiers
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## ImageService

Le service `ImageService` est responsable de la gestion des fichiers dans le système de fichiers.

### Méthodes principales

#### `saveImage(MultipartFile file, String classAppelant)`

Sauvegarde une image uploadée dans un sous-répertoire spécifique.

**Paramètres**:
- `file`: Le fichier image à sauvegarder (`MultipartFile`)
- `classAppelant`: Identifiant du sous-répertoire (ex: "utilisateur", "livre")

**Retourne**:
- Le chemin relatif de l'image sauvegardée (ex: "utilisateur/123e4567-e89b-12d3-a456-426614174000.jpg")

**Exceptions**:
- `IOException`: Si une erreur se produit lors de la sauvegarde

#### `getImage(String filename, String classAppelant)`

Récupère une image sous forme de tableau d'octets.

**Paramètres**:
- `filename`: Nom du fichier à récupérer
- `classAppelant`: Identifiant du sous-répertoire

**Retourne**:
- Un tableau d'octets contenant les données de l'image

**Exceptions**:
- `FileNotFoundException`: Si l'image n'est pas trouvée
- `IOException`: Si une erreur se produit lors de la lecture

#### `deleteImage(String filename, String classAppelant)`

Supprime une image du système de fichiers.

**Paramètres**:
- `filename`: Nom du fichier à supprimer
- `classAppelant`: Identifiant du sous-répertoire

**Retourne**:
- `true` si la suppression a réussi, `false` sinon

#### `getFullPath(String relativePath)`

Convertit un chemin relatif en chemin absolu.

**Paramètres**:
- `relativePath`: Le chemin relatif de l'image

**Retourne**:
- Le chemin absolu vers le fichier

### Exemples d'utilisation

#### Exemple 1: Sauvegarder une image de profil utilisateur

```java
@Autowired
private ImageService imageService;

@PostMapping("/mon-compte/photo")
public String changerPhotoProfile(
        @RequestParam("profileImage") MultipartFile profileImage,
        RedirectAttributes redirectAttributes) {
    try {
        // Sauvegarde l'image et récupère son chemin relatif
        String imagePath = imageService.saveImage(profileImage, "utilisateur");
        
        // Mise à jour de l'utilisateur avec le nouveau chemin d'image
        Utilisateur utilisateur = getCurrentUser();
        utilisateur.setProfileImage(imagePath);
        utilisateurService.updateUtilisateur(utilisateur);
        
        return "redirect:/mon-compte";
    } catch (IOException e) {
        // Gestion des erreurs
        return "redirect:/error";
    }
}
```

#### Exemple 2: Supprimer une image existante

```java
@Autowired
private ImageService imageService;

public boolean supprimerPhotoProfil(Utilisateur utilisateur) {
    String imagePath = utilisateur.getProfileImage();
    
    // Si l'utilisateur a une image de profil
    if (imagePath != null && !imagePath.isEmpty()) {
        // Supprime l'image du système de fichiers
        boolean deleted = imageService.deleteImage(imagePath, "utilisateur");
        
        if (deleted) {
            // Mise à jour de l'utilisateur
            utilisateur.setProfileImage(null);
            utilisateurService.updateUtilisateur(utilisateur);
        }
        
        return deleted;
    }
    
    return false;
}
```

## ImageUrlConfig

Le composant `ImageUrlConfig` est responsable de la génération des URLs pour accéder aux images.

### Fonctionnalités

- Génère des URLs complètes pour accéder aux images
- Gère l'URL par défaut pour les images manquantes

### Méthodes principales

#### `getProfileImageUrl(String imageName)`

Génère l'URL pour une image de profil.

**Paramètres**:
- `imageName`: Le chemin relatif de l'image

**Retourne**:
- L'URL complète pour accéder à l'image, ou l'URL de l'image par défaut si `imageName` est null ou vide

#### `getBaseUploadUrl()`

Retourne l'URL de base pour les fichiers uploadés.

**Retourne**:
- L'URL de base configurée (ex: "/uploads/")

#### `getDefaultAvatarUrl()`

Retourne l'URL de l'avatar par défaut.

**Retourne**:
- L'URL de l'avatar par défaut configurée (ex: "/images/default-avatar.png")

### Exemples d'utilisation

#### Exemple: Afficher l'image de profil d'un utilisateur

```java
@Autowired
private ImageUrlConfig imageUrlConfig;

@GetMapping("/utilisateurs/{id}")
public String afficherUtilisateur(@PathVariable Long id, Model model) {
    Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
    
    // Génère l'URL de l'image de profil
    String imageUrl = imageUrlConfig.getProfileImageUrl(utilisateur.getProfileImage());
    
    model.addAttribute("utilisateur", utilisateur);
    model.addAttribute("imageUrl", imageUrl);
    
    return "utilisateur/profil";
}
```

## Bonnes pratiques

1. **Toujours utiliser un sous-répertoire spécifique**: Organisez les fichiers par type en utilisant le paramètre `classAppelant`.

2. **Gérer proprement les exceptions**: Capturez et traitez les exceptions `IOException` lors de l'utilisation du service.

3. **Supprimer les anciennes images**: Lors du remplacement d'une image, n'oubliez pas de supprimer l'ancienne.

4. **Valider les types de fichiers**: Ajoutez des validations pour s'assurer que seuls les types de fichiers autorisés sont uploadés.

5. **Utiliser ImageUrlConfig pour les URLs**: Ne construisez pas manuellement les URLs d'images, utilisez `ImageUrlConfig`.

## Cas d'usage courants

### 1. Implémentation de l'upload d'images pour les livres

Pour implémenter l'upload d'images de couverture pour les livres, vous pouvez procéder comme suit:

```java
@Controller
@RequestMapping("/dashboard/livres")
public class LivreController {

    private final LivreService livreService;
    private final ImageService imageService;
    private final ImageUrlConfig imageUrlConfig;
    
    public LivreController(
            LivreService livreService,
            ImageService imageService,
            ImageUrlConfig imageUrlConfig) {
        this.livreService = livreService;
        this.imageService = imageService;
        this.imageUrlConfig = imageUrlConfig;
    }
    
    @PostMapping("/creer")
    public String creerLivre(
            @ModelAttribute @Valid Livre livre,
            BindingResult result,
            @RequestParam("couvertureFile") MultipartFile couvertureFile,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (!couvertureFile.isEmpty()) {
                // Sauvegarde l'image de couverture
                String imagePath = imageService.saveImage(couvertureFile, "livre");
                livre.setCouverture(imagePath);
            }
            
            livreService.saveLivre(livre);
            
            addSuccessMessage(redirectAttributes, "Le livre a été créé avec succès.");
            return "redirect:/dashboard/livres";
        } catch (IOException e) {
            addErrorMessage(redirectAttributes, "Une erreur s'est produite lors de l'upload de l'image.");
            return "redirect:/dashboard/livres/creer";
        }
    }
    
    @GetMapping("/{id}")
    public String afficherLivre(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreById(id);
        
        // Génère l'URL de la couverture
        String couvertureUrl = imageUrlConfig.getBaseUploadUrl() + livre.getCouverture();
        
        model.addAttribute("livre", livre);
        model.addAttribute("couvertureUrl", couvertureUrl);
        
        return "livre/details";
    }
}
```

### 2. Affichage d'images dans un template Thymeleaf

Dans vos templates Thymeleaf, vous pouvez afficher les images comme suit:

```html
<!-- Pour une image de profil utilisateur -->
<img th:src="${imageUrl}" alt="Photo de profil" class="profile-image" />

<!-- Pour une couverture de livre -->
<img th:src="${couvertureUrl}" alt="Couverture du livre" class="book-cover" />
```

### 3. Modification d'une image existante

```java
@PostMapping("/{id}/modifier-couverture")
public String modifierCouvertureLivre(
        @PathVariable Long id,
        @RequestParam("couvertureFile") MultipartFile couvertureFile,
        RedirectAttributes redirectAttributes) {
    
    try {
        Livre livre = livreService.getLivreById(id);
        
        // Supprime l'ancienne image de couverture si elle existe
        if (livre.getCouverture() != null && !livre.getCouverture().isEmpty()) {
            imageService.deleteImage(livre.getCouverture(), "livre");
        }
        
        // Sauvegarde la nouvelle image de couverture
        String imagePath = imageService.saveImage(couvertureFile, "livre");
        livre.setCouverture(imagePath);
        
        livreService.saveLivre(livre);
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "La couverture du livre a été modifiée avec succès.");
        return "redirect:/dashboard/livres/" + id;
    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Une erreur s'est produite lors de l'upload de l'image.");
        return "redirect:/dashboard/livres/" + id + "/modifier";
    }
}
```
