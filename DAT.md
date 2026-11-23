# P4 - Document d'Architecture Technique - EcoRéno

## 1. Contexte & périmètre

Outil interne d'EcoRéno pour suivre les **demandes de rénovation**, de la réception au traitement.

**Dans le périmètre :**

- Authentification simple des employés.
- Gestion des clients (création, consultation).
- Gestion des demandes de rénovation : CRUD + cycle de vie (statuts).
- Recherche / filtres sur les demandes.
- Règles de gestion et d'accès simples.

**Hors périmètre (à ne pas développer) :**

- Paiement en ligne, facturation.
- Gestion électronique de documents (GED), upload de fichiers.
- Notifications (email, SMS), reporting / tableaux de bord analytiques.
- Portail client externe.

## 2. Acteurs

- **Employé** : membre de l'équipe EcoRéno ; gère les demandes dont il est responsable.
- **Admin (lead dev / responsable)** : accès étendu ; peut réassigner les demandes.

## 3. Besoins fonctionnels (user stories)

| # | En tant que… | je veux… | afin de… |
| --- | --- | --- | --- |
| US1 | employé | me connecter | accéder à l'application en sécurité |
| US2 | employé | créer une demande rattachée à un client | enregistrer un nouveau besoin |
| US3 | employé | lister et filtrer les demandes (statut, type) | retrouver rapidement une demande |
| US4 | employé | consulter le détail d'une demande | voir toutes ses informations |
| US5 | employé | mettre à jour une demande dont je suis responsable | faire évoluer son statut et ses infos |
| US6 | admin | réassigner une demande à un autre employé | répartir la charge |

## 4. Modèle de données (MLD simplifié — 3 entités)

**Client**

| Champ | Type | Contraintes |
| --- | --- | --- |
| id | UUID / bigint | PK |
| nom | varchar | requis |
| email | varchar | requis, format email |
| telephone | varchar | optionnel |
| adresse_logement | varchar | requis |

**Utilisateur (Employé)**

| Champ | Type | Contraintes |
| --- | --- | --- |
| id | UUID / bigint | PK |
| nom | varchar | requis |
| email | varchar | requis, unique |
| mot_de_passe | varchar | requis, haché |
| role | enum(EMPLOYE, ADMIN) | requis |

**Demande de rénovation**

| Champ | Type | Contraintes |
| --- | --- | --- |
| id | UUID / bigint | PK |
| reference | varchar | requis, unique |
| description | text | requis |
| type_renovation | enum(ISOLATION, CHAUFFAGE, VENTILATION, MENUISERIES, AUTRE) | requis |
| statut | enum (voir §5) | requis |
| montant_estime | decimal | optionnel |
| date_creation | datetime | auto |
| date_maj | datetime | auto |
| client_id | FK → Client | requis |
| employe_id | FK → Utilisateur | requis |

*Relations : un client a plusieurs demandes ; un employé est responsable de plusieurs demandes.*

## 5. Cycle de vie d'une demande (workflow)

`Reçue → En qualification → Devis envoyé → Planifiée → Terminée`

Transition alternative : depuis *En qualification* ou *Devis envoyé* → **Refusée** (état terminal).

- Seules les transitions prévues sont autorisées (pas de retour arbitraire en arrière).
- *Terminée* et *Refusée* sont des états finaux.

## 6. Règles de gestion

- **RG1** : une demande est toujours rattachée à un client et à un employé responsable.
- **RG2** : le montant estimé est renseigné lors de la qualification (barème simple par type, ou saisie manuelle).
- **RG3** : seules les transitions de statut prévues par le workflow sont autorisées.
- **RG4 (accès)** : un employé ne modifie que les demandes dont il est responsable ; un admin peut modifier et réassigner toutes les demandes.

## 7. Contrats d'interface (API REST)

| Méthode | Endpoint | Accès | Corps / params | Réponses |
| --- | --- | --- | --- | --- |
| POST | /api/auth/login | public | {email, motDePasse} | 200 {token} ; 401 |
| GET | /api/demandes | authentifié | ?statut= &type= | 200 [Demande] ; 401 |
| POST | /api/demandes | authentifié | {clientId, description, type, …} | 201 Demande ; 400 ; 401 |
| GET | /api/demandes/{id} | authentifié | — | 200 Demande ; 404 |
| PUT | /api/demandes/{id} | responsable / admin | {statut?, description?, montantEstime?, …} | 200 ; 403 ; 404 |
| GET | /api/clients | authentifié | — | 200 [Client] |
| POST | /api/clients | authentifié | {nom, email, …} | 201 ; 400 |

*Conventions : échanges JSON, codes HTTP sémantiques, erreurs au format `{code, message}`.*

## 8. Maquettes à respecter

Quatre écrans :

- **Connexion** : formulaire email + mot de passe.
- **Liste des demandes** : tableau (référence, client, type, statut, responsable) + filtres statut/type + bouton « Nouvelle demande ».
- **Détail / édition d'une demande** : informations + changement de statut.
- **Création d'une demande** : formulaire + sélection du client.

<aside>

**Exigences d'accessibilité (transverses à tous les écrans) :**

- structure sémantique (landmarks, titres hiérarchisés) ;
- `<label>` associé à chaque champ et messages d'erreur reliés aux champs ;
- contrastes WCAG AA ;
- focus clavier visible et navigation complète au clavier ;
- aucune information transmise par la seule couleur.
</aside>