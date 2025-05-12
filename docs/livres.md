# Documentation - Gestion des Livres

Ce document présente une vue d'ensemble du module de gestion des livres dans l'application Biblio_Num.

## Table des matières
1. [Introduction](#introduction)
2. [Modèle](#modèle)
3. [URLs et Routes](#urls-et-routes)
4. [Fonctionnalités](#fonctionnalités)
5. [Permissions](#permissions)
6. [TODO](#todo)

## Introduction

Le module de gestion des livres permet aux administrateurs de créer, modifier, supprimer et lister les livres disponibles dans la bibliothèque numérique.

## Modèle

### Livre
- **Classe**: `Livre`
- **Attributs principaux**:
  - `id`: Long - Identifiant unique du livre
  - `titre`: String - Titre du livre
  - `couverture`: String - Couverture du livre
  - `resume`: String - Résumé du livre (optionnel)
  
**Note**: Les relations avec les auteurs et les catégories ne sont pas encore implémentées (voir section TODO).

## URLs et Routes

Le module de gestion des livres est accessible via les URLs suivantes:

| URL                            | Méthode HTTP | Description                              |
|--------------------------------|-------------|------------------------------------------|
| `/dashboard/livres`            | GET         | Liste paginée des livres                 |
| `/dashboard/livres/creer`      | GET         | Affiche le formulaire de création        |
| `/dashboard/livres/creer`      | POST        | Traite la création d'un nouveau livre    |
| `/dashboard/livres/{id}/modifier` | GET      | Affiche le formulaire de modification    |
| `/dashboard/livres/{id}/modifier` | POST     | Traite la modification d'un livre        |
| `/dashboard/livres/{id}/supprimer` | GET     | Affiche la confirmation de suppression   |
| `/dashboard/livres/{id}/supprimer` | POST    | Traite la suppression d'un livre         |

## Fonctionnalités

### Liste des livres
- Affichage de tous les livres
- Recherche par titre
- Pagination et tri intégrés

### Création de livre
- Formulaire de création avec validation
- Le titre est requis, le résumé est optionnel
- Messages d'erreur contextuels

### Modification de livre
- Formulaire pré-rempli avec les données existantes
- Validation similaire à la création
- Possibilité de modifier le titre et le résumé

### Suppression de livre
- Confirmation avant suppression
- Messages de succès après suppression

## Permissions

L'accès au module de gestion des livres est sécurisé:

- **Rôles autorisés**: 
  - ADMINISTRATEUR
- **Annotation de sécurité**: `@PreAuthorize("hasRole('ADMINISTRATEUR')")`

## Validation

Lors de la création ou de la modification d'un livre, les validations suivantes sont effectuées:

1. Le titre ne peut pas être vide
2. Le résumé n'a pas de contraintes spécifiques

## Exemples d'utilisation

### Créer un nouveau livre
1. Accéder à `/dashboard/livres`
2. Cliquer sur "Ajouter un livre"
3. Remplir le formulaire avec un titre (obligatoire) et un résumé (optionnel)
4. Soumettre le formulaire

### Modifier un livre existant
1. Accéder à `/dashboard/livres`
2. Cliquer sur "Modifier" à côté du livre
3. Mettre à jour les informations
4. Soumettre le formulaire

### Supprimer un livre
1. Accéder à `/dashboard/livres`
2. Cliquer sur "Supprimer" à côté du livre
3. Confirmer la suppression

## TODO

### Relations avec les auteurs et catégories
- [ ] Implémenter la relation entre les livres et les auteurs
- [ ] Implémenter la relation entre les livres et les catégories
- [ ] Ajouter un sélecteur d'auteur(s) dans le formulaire de création/modification
- [ ] Ajouter un sélecteur de catégorie(s) dans le formulaire de création/modification
- [ ] Développer la fonctionnalité de filtrage des livres par auteur et catégorie

### Gestion des images
- [ ] Ajouter la possibilité d'uploader une image de couverture pour les livres
- [ ] Implémenter le stockage des images
- [ ] Ajouter une prévisualisation des images dans les formulaires
- [ ] Gérer la suppression des images lors de la suppression d'un livre

### Améliorations de l'interface de gestion
- [ ] Améliorer le tri par titre, auteur et catégorie
- [ ] Rendre configurable le nombre d'éléments par page
- [ ] Améliorer le système de recherche (recherche avancée, filtres)
- [ ] Ajouter un panneau de filtres par auteur et catégorie

### Fonctionnalités additionnelles
- [ ] Ajouter des champs supplémentaires: année de publication, ISBN, nombre de pages
- [ ] Implémenter un système d'évaluation des livres par les utilisateurs
- [ ] Ajouter la possibilité de télécharger le livre au format numérique
- [ ] Développer une interface de visualisation détaillée d'un livre
