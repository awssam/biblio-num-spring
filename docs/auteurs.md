# Documentation - Gestion des Auteurs

Ce document présente une vue d'ensemble du module de gestion des auteurs dans l'application Biblio_Num.

## Table des matières
1. [Introduction](#introduction)
2. [Modèle](#modèle)
3. [URLs et Routes](#urls-et-routes)
4. [Fonctionnalités](#fonctionnalités)
5. [Permissions](#permissions)
6. [TODO](#todo)

## Introduction

Le module de gestion des auteurs permet aux administrateurs de créer, modifier, supprimer et lister les auteurs des livres disponibles dans la bibliothèque numérique.

## Modèle

### Auteur
- **Classe**: `Auteur`
- **Attributs principaux**:
  - `id`: Long - Identifiant unique de l'auteur
  - `nom`: String - Nom de l'auteur
  - `prenom`: String - Prénom de l'auteur (optionnel)
  - `biographie`: String - Biographie de l'auteur (optionnelle)

## URLs et Routes

Le module de gestion des auteurs est accessible via les URLs suivantes:

| URL                            | Méthode HTTP | Description                              |
|--------------------------------|-------------|------------------------------------------|
| `/dashboard/auteurs`           | GET         | Liste paginée des auteurs                |
| `/dashboard/auteurs/creer`     | GET         | Affiche le formulaire de création        |
| `/dashboard/auteurs/creer`     | POST        | Traite la création d'un nouvel auteur    |
| `/dashboard/auteurs/{id}/modifier` | GET    | Affiche le formulaire de modification    |
| `/dashboard/auteurs/{id}/modifier` | POST   | Traite la modification d'un auteur       |
| `/dashboard/auteurs/{id}/supprimer` | GET   | Affiche la confirmation de suppression   |
| `/dashboard/auteurs/{id}/supprimer` | POST  | Traite la suppression d'un auteur        |

## Fonctionnalités

### Liste des auteurs
- Affichage de tous les auteurs
- Recherche par nom et prénom d'auteur
- Pagination et tri intégrés

### Création d'auteur
- Formulaire de création avec validation
- Seul le nom est requis, le prénom et la biographie sont optionnels
- Messages d'erreur contextuels

### Modification d'auteur
- Formulaire pré-rempli avec les données existantes
- Validation similaire à la création
- Possibilité de modifier le nom, le prénom et la biographie

### Suppression d'auteur
- Confirmation avant suppression
- Gestion des erreurs si l'auteur est associé à des livres

## Permissions

L'accès au module de gestion des auteurs sera sécurisé (à implémenter):

- **Rôles autorisés** (à configurer): 
  - ADMINISTRATEUR
- **Annotation de sécurité** (prévue): `@PreAuthorize("hasRole('ADMINISTRATEUR')")`

**Note**: La sécurité n'est pas encore implémentée et est référencée uniquement dans le code sous forme d'annotations (voir section TODO).

## Validation

Lors de la création ou de la modification d'un auteur, les validations suivantes sont effectuées:

1. Le prenom ne peut pas être vide
2. La biographie ne doit pas dépasser une longueur maximale (à définir)

## Exemples d'utilisation

### Créer un nouvel auteur
1. Accéder à `/dashboard/auteurs`
2. Cliquer sur "Créer un auteur"
3. Remplir le formulaire avec un prénom (obligatoire), un nom (optionnel) et une biographie (optionnelle)
4. Soumettre le formulaire

### Modifier un auteur existant
1. Accéder à `/dashboard/auteurs`
2. Cliquer sur "Modifier" à côté de l'auteur
3. Mettre à jour les informations
4. Soumettre le formulaire

### Supprimer un auteur
1. Accéder à `/dashboard/auteurs`
2. Cliquer sur "Supprimer" à côté de l'auteur
3. Confirmer la suppression

**Note**: La gestion des erreurs pour la suppression lorsqu'il existe des livres associés sera implémentée ultérieurement (voir section TODO).

## TODO

### Sécurité
- [ ] Implémenter la véritable sécurité basée sur les rôles (actuellement en annotation mais non fonctionnelle)
- [ ] Restreindre l'accès à l'administration des auteurs aux utilisateurs administrateurs uniquement
- [ ] Ajouter la journalisation des actions (création, modification, suppression)

### Améliorations de l'interface de gestion
- [ ] Améliorer le tri par nom et prénom
- [ ] Rendre configurable le nombre d'éléments par page
- [ ] Améliorer le système de recherche (recherche avancée, filtres)

### Relations avec les livres
- [ ] Implémenter la relation entre les auteurs et les livres
- [ ] Gérer la contrainte d'intégrité référentielle lors de la suppression d'un auteur
- [ ] Ajouter un compteur de livres associés à chaque auteur
- [ ] Développer la fonctionnalité d'affichage des livres par auteur

### Fonctionnalités additionnelles
- [ ] Ajouter la possibilité d'uploader une photo de l'auteur
- [ ] Intégrer des liens vers des sources externes (Wikipédia, réseaux sociaux)
- [ ] Implémenter un système de classement des auteurs par popularité
- [ ] Permettre l'ajout de notes/commentaires internes sur les auteurs pour les administrateurs
