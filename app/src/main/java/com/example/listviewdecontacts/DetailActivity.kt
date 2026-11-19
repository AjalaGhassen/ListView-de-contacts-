package com.example.listviewdecontacts

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var ivPhoto: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTelephone: EditText
    private lateinit var etEmail: EditText

    private lateinit var btnGalerie: Button
    private lateinit var btnCamera: Button
    private lateinit var btnAppeler: Button
    private lateinit var btnEmail: Button
    private lateinit var btnEnregistrer: Button
    private lateinit var btnAnnuler: Button

    private var currentContact: Contact? = null
    private var contactPosition: Int = -1
    private var currentPhotoUri: String? = null

    // Launcher pour la sélection de photo depuis la galerie
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentPhotoUri = uri.toString()
            ivPhoto.setImageURI(uri)
        } else {
            Toast.makeText(this, "Sélection d'image annulée", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher pour la prise de photo avec l'appareil photo (retourne un Bitmap)
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val savedUri = saveBitmapToInternalStorage(bitmap)
            if (savedUri != null) {
                currentPhotoUri = savedUri.toString()
                ivPhoto.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "Prise de photo annulée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.title = getString(R.string.title_detail_contact)

        // Récupération des données transmises par MainActivity
        contactPosition = intent.getIntExtra("position", -1)
        @Suppress("DEPRECATION")
        currentContact = intent.getSerializableExtra("contact") as? Contact

        if (currentContact == null) {
            finish()
            return
        }

        // Initialisation des vues
        ivPhoto = findViewById(R.id.ivDetailPhoto)
        etNom = findViewById(R.id.etNom)
        etPrenom = findViewById(R.id.etPrenom)
        etTelephone = findViewById(R.id.etTelephone)
        etEmail = findViewById(R.id.etEmail)

        btnGalerie = findViewById(R.id.btnGalerie)
        btnCamera = findViewById(R.id.btnCamera)
        btnAppeler = findViewById(R.id.btnAppeler)
        btnEmail = findViewById(R.id.btnEmail)
        btnEnregistrer = findViewById(R.id.btnEnregistrer)
        btnAnnuler = findViewById(R.id.btnAnnuler)

        // Remplissage des champs
        val contact = currentContact!!
        etNom.setText(contact.nom)
        etPrenom.setText(contact.prenom)
        etTelephone.setText(contact.telephone)
        etEmail.setText(contact.email)
        currentPhotoUri = contact.photoUri

        if (!currentPhotoUri.isNullOrEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(currentPhotoUri))
            } catch (e: Exception) {
                ivPhoto.setImageResource(contact.imageResId)
            }
        } else {
            ivPhoto.setImageResource(contact.imageResId)
        }

        // Configuration des événements
        btnGalerie.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        btnAppeler.setOnClickListener {
            lancerAppel()
        }

        btnEmail.setOnClickListener {
            lancerEmail()
        }

        btnEnregistrer.setOnClickListener {
            enregistrerModifications()
        }

        btnAnnuler.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun lancerAppel() {
        val tel = etTelephone.text.toString().trim()
        if (tel.isEmpty()) {
            etTelephone.error = getString(R.string.err_champ_obligatoire)
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_aucune_app_téléphone, Toast.LENGTH_SHORT).show()
        }
    }

    private fun lancerEmail() {
        val mail = etEmail.text.toString().trim()
        if (mail.isEmpty()) {
            etEmail.error = getString(R.string.err_champ_obligatoire)
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$mail"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.msg_aucune_app_email, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enregistrerModifications() {
        val nom = etNom.text.toString().trim()
        val prenom = etPrenom.text.toString().trim()
        val telephone = etTelephone.text.toString().trim()
        val email = etEmail.text.toString().trim()

        var isValid = true

        if (nom.isEmpty()) {
            etNom.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (prenom.isEmpty()) {
            etPrenom.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (telephone.isEmpty()) {
            etTelephone.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }
        if (email.isEmpty()) {
            etEmail.error = getString(R.string.err_champ_obligatoire)
            isValid = false
        }

        if (!isValid) return

        val contactModifie = currentContact!!.copy(
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = email,
            photoUri = currentPhotoUri
        )

        val resultIntent = Intent()
        resultIntent.putExtra("contact", contactModifie)
        resultIntent.putExtra("position", contactPosition)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): Uri? {
        val filename = "contact_photo_${System.currentTimeMillis()}.jpg"
        val file = File(cacheDir, filename)
        return try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.flush()
            stream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}