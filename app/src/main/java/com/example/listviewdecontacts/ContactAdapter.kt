package com.example.listviewdecontacts

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val contacts: List<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_contact, parent, false
        )

        val contact = getItem(position) ?: return view

        val ivPhoto = view.findViewById<ImageView>(R.id.ivPhotoItem)
        val tvNomPrenom = view.findViewById<TextView>(R.id.tvNomPrenomItem)
        val tvTelephone = view.findViewById<TextView>(R.id.tvTelephoneItem)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmailItem)

        tvNomPrenom.text = "${contact.prenom} ${contact.nom}"
        tvTelephone.text = contact.telephone
        tvEmail.text = contact.email

        if (!contact.photoUri.isNullOrEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(contact.photoUri))
            } catch (e: Exception) {
                ivPhoto.setImageResource(contact.imageResId)
            }
        } else {
            ivPhoto.setImageResource(contact.imageResId)
        }

        return view
    }
}