package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.CedricLucieFlorent.benfit.Models.Exercice
import kotlinx.android.synthetic.main.activity_exercice.*
import java.util.*

class ExerciceActivity : MenuActivity() {


    private lateinit var stu: StreamToUri
    private lateinit var storageReference: StorageReference
    private var imageUri : Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_exercice, frameLayout)
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid

        stu = StreamToUri(this, this, contentResolver)
        storageReference = FirebaseStorage.getInstance().reference


        createSpinnerCategory()
        createSpinnerLevel()

        imgViewExo.visibility = View.INVISIBLE
        inputURLExo.visibility = View.VISIBLE

        urlButton.setOnClickListener {
            imgViewExo.visibility = View.INVISIBLE
            inputURLExo.visibility = View.VISIBLE
        }

        imgButton.setOnClickListener {
            inputURLExo.visibility = View.INVISIBLE
            imgViewExo.visibility = View.VISIBLE
            imgViewExo.setOnClickListener {
                stu.askCameraPermissions()
            }
        }

        btnDoneExo.setOnClickListener {
            val nameExo: String = inputNameExo.text.toString()
            val descExo: String = inputDescExo.text.toString()
            val categoryExo: String = spinnerSportExo.selectedItem.toString()
            val levelExo: String = spinnerLevelExo.selectedItem.toString()
            val urlExo: String
            val valid: Boolean = constraintValidateYoutube(urlButton, inputURLExo.text.toString())

            if (valid){
            val resRequest =
                id?.let { it1 ->
                    addNewExo(
                        database, nameExo,
                        it1, descExo, levelExo, categoryExo
                    )
                }

            if (resRequest != "false") {
                if (urlButton.isChecked) {
                    urlExo = inputURLExo.text.toString()
                    database.getReference("exos/${resRequest}/urlYt").setValue(urlExo)
                }
                else {
                    val uniqID = UUID.randomUUID().toString()
                    val stoRef = storageReference.child("exos/${resRequest}/${uniqID}")
                    val result: UploadTask
                    result = if (imageUri != Uri.EMPTY) {
                        stoRef.putFile(imageUri)
                    } else {
                        val uri = getDrawableToURI(context, R.drawable.exercice)
                        stoRef.putFile(uri)
                    }
                    result.addOnSuccessListener {
                        database.getReference("exos/${resRequest}/pictureUID").setValue(uniqID)
                    }
                }
                Toast.makeText(this, "Nouvel exercice créé !", Toast.LENGTH_SHORT).show()
                val exo: Exercice? = resRequest?.let { it1 ->
                    Exercice(
                        it1,
                        nameExo,
                        id.toString(),
                        descExo,
                        levelExo,
                        ""
                    )
                }
                if (exo != null) {
                    addTemporaryExoSession(database, id.toString(), exo)
                }
                intent = Intent(this, SessionActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Erreur! Veuillez réessayer!", Toast.LENGTH_SHORT).show()
            }
            }
            else {
                toast(context, "Veuillez rensigner un URL correct")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stu.manageActivityResult(requestCode, data)
        imageUri = stu.imageUri
        imgViewExo.setImageURI(imageUri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }

    private fun createSpinnerLevel(){
        val spinner: Spinner = findViewById(R.id.spinnerLevelExo)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.level_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun createSpinnerCategory(){
        val spinner: Spinner = findViewById(R.id.spinnerSportExo)
        ArrayAdapter.createFromResource(
            this,
            R.array.category_exo_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
}
