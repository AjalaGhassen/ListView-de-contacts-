package com.example.listviewdecontacts

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 1) Les données : la liste initiale de l'énoncé du TP
    private val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami"
    )

    // 2) L'adaptateur fait le lien entre les données et la ListView
    private lateinit var adapter: ArrayAdapter<String>

    // 3) Les vues
    private lateinit var etContact: EditText
    private lateinit var btnAjouter: Button
    private lateinit var btnModifier: Button
    private lateinit var lvContacts: ListView

    // Position de l'élément sélectionné (-1 = aucune sélection)
    private var positionSelectionnee = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restauration de l'état lors de la rotation de l'écran
        if (savedInstanceState != null) {
            val savedContacts = savedInstanceState.getStringArrayList("contacts")
            if (savedContacts != null) {
                contacts.clear()
                contacts.addAll(savedContacts)
            }
            positionSelectionnee = savedInstanceState.getInt("positionSelectionnee", -1)
        }

        // Récupération des vues déclarées dans activity_main.xml
        etContact = findViewById(R.id.etContact)
        btnAjouter = findViewById(R.id.btnAjouter)
        btnModifier = findViewById(R.id.btnModifier)
        lvContacts = findViewById(R.id.lvContacts)

        // Création de l'adaptateur avec le layout simple_list_item_activated_1
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )
        lvContacts.adapter = adapter

        // Si une sélection existait avant la rotation, la réactiver
        if (positionSelectionnee in contacts.indices) {
            lvContacts.setItemChecked(positionSelectionnee, true)
            etContact.setText(contacts[positionSelectionnee])
        }

        // Clic sur un élément de la liste → copie du texte dans l'EditText
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            positionSelectionnee = position
            lvContacts.setItemChecked(position, true)
            etContact.setText(contacts[position])
            etContact.setSelection(etContact.text.length) // curseur à la fin
            etContact.requestFocus()
        }

        // Appui long sur un élément → confirmation de suppression
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->
            afficherDialogueSuppression(position)
            true
        }

        // Clic sur le bouton "Ajouter" (Exercice 6.1)
        btnAjouter.setOnClickListener { ajouterContact() }

        // Clic sur le bouton "Modifier"
        btnModifier.setOnClickListener { modifierContact() }

        // Touche "Terminé" du clavier : modifie si sélection, sinon ajoute
        etContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (positionSelectionnee != -1) {
                    modifierContact()
                } else {
                    ajouterContact()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("contacts", ArrayList(contacts))
        outState.putInt("positionSelectionnee", positionSelectionnee)
    }

    private fun ajouterContact() {
        val nom = etContact.text.toString().trim()
        if (nom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Exercice 6.3 : Vérification des doublons
        if (contacts.any { it.equals(nom, ignoreCase = true) }) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        contacts.add(nom)
        // Exercice 6.4 : Tri par ordre alphabétique après modification/ajout
        contacts.sort()
        adapter.notifyDataSetChanged()

        // Sélectionner le nouvel élément ajouté après le tri
        positionSelectionnee = contacts.indexOf(nom)
        if (positionSelectionnee != -1) {
            lvContacts.setItemChecked(positionSelectionnee, true)
        }

        Toast.makeText(this, "Contact ajouté : $nom", Toast.LENGTH_SHORT).show()
    }

    private fun modifierContact() {
        // Section 4.4 : Vérification 1 - Un élément doit être sélectionné
        if (positionSelectionnee == -1) {
            Toast.makeText(this, R.string.msg_aucune_selection, Toast.LENGTH_SHORT).show()
            return
        }

        // Section 4.4 : Vérification 2 - Le texte ne doit pas être vide
        val nouveauNom = etContact.text.toString().trim()
        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Exercice 6.3 : Refuser si le nom existe déjà ailleurs dans la liste
        val estDoublon = contacts.indices.any { index ->
            index != positionSelectionnee && contacts[index].equals(nouveauNom, ignoreCase = true)
        }
        if (estDoublon) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        // Mise à jour de la donnée
        contacts[positionSelectionnee] = nouveauNom

        // Exercice 6.4 : Tri par ordre alphabétique après modification
        contacts.sort()

        // Retrouver la nouvelle position après le tri
        positionSelectionnee = contacts.indexOf(nouveauNom)

        // Notification de l'adaptateur pour redessiner la liste
        adapter.notifyDataSetChanged()
        if (positionSelectionnee != -1) {
            lvContacts.setItemChecked(positionSelectionnee, true)
        }

        Toast.makeText(this, "Contact modifié : $nouveauNom", Toast.LENGTH_SHORT).show()
    }

    private fun afficherDialogueSuppression(position: Int) {
        val nomContact = contacts[position]
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_supprimer_titre)
            .setMessage(getString(R.string.dialog_supprimer_msg, nomContact))
            .setPositiveButton(R.string.btn_oui) { _, _ ->
                contacts.removeAt(position)
                if (positionSelectionnee == position) {
                    positionSelectionnee = -1
                    etContact.text.clear()
                    lvContacts.clearChoices()
                } else if (positionSelectionnee > position) {
                    positionSelectionnee--
                }

                adapter.notifyDataSetChanged()

                if (positionSelectionnee in contacts.indices) {
                    lvContacts.setItemChecked(positionSelectionnee, true)
                }

                Toast.makeText(this, "Contact supprimé : $nomContact", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_non, null)
            .show()
    }
}