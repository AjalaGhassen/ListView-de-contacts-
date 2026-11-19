# Compte Rendu — TP Android Liste de Contacts (Version 2)

**Étudiant :** Ghassen Ajala  
**Dépôt GitHub :** [https://github.com/AjalaGhassen/ListView-de-contacts-](https://github.com/AjalaGhassen/ListView-de-contacts-)  
**Technologies :** Kotlin, Android SDK (API 24+), AppCompat, XML Views, Intents, Activity Result API.

---

## 1. Description du projet
Cette application Android est la **Version 2** du TP Liste de Contacts. Elle permet de gérer un répertoire téléphonique complet grâce à une architecture multi-activités (`MainActivity` et `DetailActivity`).

---

## 2. Fonctionnalités réalisées

### A. Modèle de données (`Contact.kt`)
- Définition d'un modèle de contact complet :
  - `id` : Identifiant unique du contact
  - `nom` & `prenom` : Noms du contact
  - `telephone` : Numéro de téléphone
  - `email` : Adresse e-mail
  - `photoUri` : URI de la photo personnalisée (Galerie ou Caméra)
  - `imageResId` : Image d'avatar par défaut (`ic_default_avatar`)
- Implémentation de l'interface `Serializable` pour permettre la transmission des objets `Contact` entre Activités via les `Intent`.

### B. Interface principale (`MainActivity`)
- **ListView personnalisée** : Utilisation d'un `ContactAdapter` héritant de `ArrayAdapter<Contact>` et d'un layout sur-mesure `item_contact.xml`.
- Chaque ligne affiche :
  - La photo / avatar du contact.
  - Le prénom et le nom en gras.
  - Le numéro de téléphone.
  - L'adresse e-mail.
- **Intent Explicite & Retour de résultat** : Un clic sur n'importe quel contact lance `DetailActivity` via `ActivityResultLauncher` (`StartActivityForResult`), en lui passant l'objet `Contact` et sa position.
- **Mise à jour en temps réel** : À la réception du résultat (`RESULT_OK`), la liste est mise à jour immédiatement (`notifyDataSetChanged()`).

### C. Écran de détail & Édition (`DetailActivity`)
- **Modification des informations** : Permet d'afficher et de modifier le nom, le prénom, le téléphone et l'e-mail.
- **Gestion des photos** :
  - **Galerie** : Sélection d'une image avec `ActivityResultContracts.GetContent()`.
  - **Caméra** : Prise d'une photo avec `ActivityResultContracts.TakePicturePreview()`, enregistrement local et affichage direct dans l' `ImageView`.
- **Intents Implicites** :
  - **Bouton Appeler** : Déclenche l'action `Intent.ACTION_DIAL` avec l'URI `tel:<numero>`.
  - **Bouton Envoyer E-mail** : Déclenche l'action `Intent.ACTION_SENDTO` avec l'URI `mailto:<email>`.
- **Enregistrer & Annuler** :
  - **Enregistrer** : Vérifie la validité des champs (non vides), prépare l'Intent de retour (`RESULT_OK`) avec le contact modifié et ferme l'activité.
  - **Annuler** : Renvoie `RESULT_CANCELED` et ferme l'activité sans conserver les modifications.

---

## 3. Architecture des fichiers du projet
```
app/src/main/
├── java/com/example/listviewdecontacts/
│   ├── Contact.kt            # Modèle de données (Serializable)
│   ├── ContactAdapter.kt     # Adaptateur personnalisé pour la ListView
│   ├── MainActivity.kt       # Liste des contacts & gestion du retour de résultat
│   └── DetailActivity.kt     # Affichage, modification, galerie/caméra, intents
└── res/
    ├── layout/
    │   ├── activity_main.xml   # Layout principal avec ListView
    │   ├── activity_detail.xml # Layout de détail/édition complet
    │   └── item_contact.xml    # Layout d'une ligne de la ListView
    ├── drawable/               # Icônes vectorielles (avatar, appel, email, caméra, galerie)
    └── values/
        └── strings.xml         # Ressources de chaînes de caractères
```

---

## 4. Tests effectués
1. **Ouverture & Modification d'un contact** : Sélection d'un contact dans `MainActivity`, modification du nom/prénom dans `DetailActivity`, clic sur **Enregistrer** -> La liste dans `MainActivity` est immédiatement mise à jour.
2. **Appels et E-mails** : Clic sur **Appeler** ouvre le composeur téléphonique avec le numéro prérempli. Clic sur **Envoyer E-mail** ouvre l'application de messagerie.
3. **Galerie & Appareil photo** : Choix d'une photo dans la galerie ou prise de photo via la caméra -> La photo s'affiche immédiatement et est sauvegardée dans le contact.
4. **Annulation** : Clic sur **Annuler** revient à `MainActivity` sans appliquer de changement.
5. **Validation des champs obligatoires** : Si un champ est vidé, une erreur `error` s'affiche sur l' `EditText` concerné et l'enregistrement est bloqué.
