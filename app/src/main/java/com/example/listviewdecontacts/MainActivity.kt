package com.example.listviewdecontacts

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val contacts = mutableListOf(
        Contact(1, "Ben Ali", "Ahmed", "+216 98 123 456", "ahmed.benali@example.com"),
        Contact(2, "Trabelsi", "Sarra", "+216 22 987 654", "sarra.trabelsi@example.com"),
        Contact(3, "Gharbi", "Mohamed", "+216 55 456 789", "mohamed.gharbi@example.com"),
        Contact(4, "Jaziri", "Ines", "+216 20 321 654", "ines.jaziri@example.com"),
        Contact(5, "Mansouri", "Youssef", "+216 97 654 321", "youssef.mansouri@example.com"),
        Contact(6, "Chaabane", "Amira", "+216 50 111 222", "amira.chaabane@example.com")
    )

    private lateinit var adapter: ContactAdapter
    private lateinit var lvContacts: ListView

    // Launcher pour récupérer les résultats de DetailActivity
    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val position = data.getIntExtra("position", -1)
            @Suppress("DEPRECATION")
            val contactModifie = data.getSerializableExtra("contact") as? Contact

            if (position in contacts.indices && contactModifie != null) {
                contacts[position] = contactModifie
                adapter.notifyDataSetChanged()
                Toast.makeText(this, R.string.msg_contact_mis_a_jour, Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.msg_action_annulee, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restauration lors des rotations d'écran
        if (savedInstanceState != null) {
            @Suppress("DEPRECATION")
            val savedContacts = savedInstanceState.getSerializable("contacts_list") as? ArrayList<Contact>
            if (savedContacts != null) {
                contacts.clear()
                contacts.addAll(savedContacts)
            }
        }

        lvContacts = findViewById(R.id.lvContacts)
        adapter = ContactAdapter(this, contacts)
        lvContacts.adapter = adapter

        // Clic sur un contact pour ouvrir DetailActivity
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("contact", contacts[position])
            intent.putExtra("position", position)
            detailLauncher.launch(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("contacts_list", ArrayList(contacts))
    }
}