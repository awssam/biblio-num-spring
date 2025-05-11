# Documentation - Gestion des Catégories

Ce document présente une vue d'ensemble du module de gestion des catégories dans l'application Biblio_Num.

## Table des matières
1. [Introduction](#introduction)
2. [Modèle](#modèle)
3. [URLs et Routes](#urls-et-routes)
4. [Fonctionnalités](#fonctionnalités)
5. [Permissions](#permissions)
6. [TODO](#todo)

## Introduction

Le module de gestion des catégories permet aux administrateurs et aux membres de créer, modifier, supprimer et lister les catégories de livres disponibles dans la bibliothèque numérique.

## Modèle

### Catégorie
- **Classe**: `Categorie`
- **Attributs principaux**:
  - `id`: Long - Identifiant unique de la catégorie
  - `nom`: String - Nom de la catégorie (doit être unique)

## URLs et Routes

Le module de gestion des catégories est accessible via les URLs suivantes:

| URL                                | Méthode HTTP | Description                                 |
|------------------------------------|-------------|---------------------------------------------|
| `/dashboard/categories`            | GET         | Liste paginée des catégories                |
| `/dashboard/categories/creer`      | GET         | Affiche le formulaire de création           |
| `/dashboard/categories/creer`      | POST        | Traite la création d'une nouvelle catégorie |
| `/dashboard/categories/{id}/modifier` | GET      | Affiche le formulaire de modification       |
| `/dashboard/categories/{id}/modifier` | POST     | Traite la modification d'une catégorie      |
| `/dashboard/categories/{id}/supprimer` | GET     | Affiche la confirmation de suppression      |
| `/dashboard/categories/{id}/supprimer` | POST    | Traite la suppression d'une catégorie       |

## Fonctionnalités

### Liste des catégories
- Affichage de toutes les catégories
- Recherche par nom de catégorie
- Pagination et tri prévus (voir section TODO)

### Création de catégorie
- Formulaire de création avec validation
- Vérification de l'unicité du nom de la catégorie
- Messages d'erreur contextuels

### Modification de catégorie
- Formulaire pré-rempli avec les données existantes
- Validation similaire à la création
- Vérification que le nouveau nom n'est pas déjà utilisé par une autre catégorie

### Suppression de catégorie
- Confirmation avant suppression
- Gestion des erreurs si la catégorie est associée à des livres

## Permissions

L'accès au module de gestion des catégories sera sécurisé (à implémenter):

- **Rôles autorisés** (à configurer): 
  - MEMBRE
  - ADMINISTRATEUR
- **Annotation de sécurité** (prévue): `@PreAuthorize("hasRole('MEMBRE') or hasRole('ADMINISTRATEUR')")`

**Note**: La sécurité n'est pas encore implémentée et est référencée uniquement dans le code sous forme d'annotations (voir section TODO).

## Validation

Lors de la création ou de la modification d'une catégorie, les validations suivantes sont effectuées:

1. Le nom ne peut pas être vide
2. Le nom doit être unique dans le système

## Exemples d'utilisation

### Créer une nouvelle catégorie
1. Accéder à `/dashboard/categories`
2. Cliquer sur "Créer une nouvelle catégorie"
3. Remplir le formulaire avec un nom unique
4. Soumettre le formulaire

### Modifier une catégorie existante
1. Accéder à `/dashboard/categories`
2. Cliquer sur "Modifier" à côté de la catégorie
3. Mettre à jour le nom
4. Soumettre le formulaire

### Supprimer une catégorie
1. Accéder à `/dashboard/categories`
2. Cliquer sur "Supprimer" à côté de la catégorie
3. Confirmer la suppression

**Note**: La gestion des erreurs pour la suppression lorsqu'il existe des livres associés sera implémentée ultérieurement (voir section TODO).

## TODO

### Sécurité
- [ ] Implémenter la véritable sécurité basée sur les rôles (actuellement en annotation mais non fonctionnelle)
- [ ] Restreindre l'accès à l'administration des catégories aux utilisateurs administrateurs uniquement
- [ ] Ajouter la journalisation des actions (création, modification, suppression)

### Améliorations de l'interface de gestion
- [ ] Implémenter la pagination pour la liste des catégories
- [ ] Ajouter le tri par ordre alphabétique du nom
- [ ] Rendre configurable le nombre d'éléments par page
- [ ] Améliorer le système de recherche (recherche avancée, filtres)

### Relations avec les livres
- [ ] Implémenter la relation entre les catégories et les livres
- [ ] Gérer la contrainte d'intégrité référentielle lors de la suppression d'une catégorie
- [ ] Ajouter un compteur de livres associés à chaque catégorie
- [ ] Développer la fonctionnalité d'affichage des livres par catégorie
