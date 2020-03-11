package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import isen.CedricLucieFlorent.benfit.Functions.*
import kotlinx.android.synthetic.main.activity_session.*
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.util.*
import kotlin.collections.ArrayList


class SessionActivity : MenuActivity(){

    var array : ArrayList<String> ?= ArrayList()
    private lateinit var stu: StreamToUri
    private lateinit var storageReference: StorageReference
    private var image_uri : Uri = Uri.EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_session, frameLayout)
        stu = StreamToUri(this, this, contentResolver)
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val idUser = auth.currentUser?.uid
        if (idUser != null) {
            showInfosSession(database, this, idUser)
            showExosSession(database, recyclerViewExoSession, idUser,this)
        }
        recyclerViewExoSession.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        createSpinnerLevel()
        showPopMenuExo()

        btnMinusRound.setOnClickListener {
            var round = editTextNumberSerie.text.toString().toInt()
            round -= 1
            editTextNumberSerie.setText(round.toString())
        }

        btnPlusRound.setOnClickListener {
            var round = editTextNumberSerie.text.toString().toInt()
            round += 1
            editTextNumberSerie.setText(round.toString())
        }

        imageViewCreateSession.setOnClickListener{
            stu.askCameraPermissions()
        }

        btnSaveSession.setOnClickListener {
            if (idUser != null) {
                saveSession(database,
                    storageReference,
                    image_uri,
                    context,
                    idUser,
                    inputNameSession.text.toString(),
                    inputDescSession.text.toString(),
                    spinnerLevelSession.selectedItem.toString(),
                    editTextNumberSerie.text.toString().toInt() )
                Toast.makeText(this,"Séance sauvegardée!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this,SessionFeedActivity::class.java)
                
                finish()

                startActivity(intent)

                deleteExoSessionTemp(database, idUser)
                deleteInfosTempSession(database,this, idUser)

                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }

        spinnerLevelSession.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (idUser != null) {
                    addTemporaryLevelSession(database,idUser, selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        inputNameSession.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (idUser != null) {
                    addTemporaryNameSession(database,idUser,inputNameSession.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        inputDescSession.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (idUser != null) {
                    addTemporaryDescSession(database,idUser,inputDescSession.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        editTextNumberSerie.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (idUser != null) {
                    addTemporaryRoundSession(database,idUser,editTextNumberSerie.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stu.manageActivityResult(requestCode, data)
        image_uri = stu.imageUri
        imageViewCreateSession.setImageURI(image_uri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }


    fun showPopMenuExo(){
        buttonAddExoSession.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                   R.id.menu_create_exo -> {
                        val intent = Intent(this,ExerciceActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    R.id.menu_choose_exo -> {
                        val intent = Intent(this,ListExercicesActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_exo)
            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception){
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }
    }
    fun createSpinnerLevel(){
        val spinner: Spinner = findViewById(R.id.spinnerLevelSession)

        ArrayAdapter.createFromResource(
            this,
            R.array.level_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {}

            override fun onNothingSelected(parent: AdapterView<*>?)
            {}
        }
    }


}
