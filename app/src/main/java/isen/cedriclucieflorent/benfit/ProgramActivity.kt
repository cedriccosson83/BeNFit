package isen.cedriclucieflorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import isen.cedriclucieflorent.benfit.functions.*
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : MenuActivity() {

    private lateinit var stu: StreamToUri
    private var imageUri : Uri = Uri.EMPTY
    private lateinit var storageReference : StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_program, frameLayout)
        showPopMenuSession()
        createSpinnerLevel()
        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        stu = StreamToUri(this, this, contentResolver)
        val id = auth.currentUser?.uid
        if (id != null) {
            showInfosProgram(database, this,id)
            showSessionsProgram(database,recyclerViewSessionProgram,id)
        }
        imageViewCreateProg.setOnClickListener{
            stu.askCameraPermissions()
        }
        recyclerViewSessionProgram.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        btnSaveProgram.setOnClickListener {
            if (id != null) {
                saveProgram(database, storageReference,imageUri,context, id,inputNameProgram.text.toString(),inputDescProgram.text.toString(),spinnerLevelProgram.selectedItem.toString() )
                deleteSessionsTempProgram(database,id)
                deleteInfosTempProgram(database,this, id)
                val intent = Intent(this,ProgramFeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        spinnerLevelProgram.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id1: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (id != null) {
                    addTemporaryLevelProgram(database,id, selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        inputNameProgram.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (id != null) {
                    addTemporaryNameProgram(database,id,inputNameProgram.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        inputDescProgram.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (id != null) {
                    addTemporaryDescProgram(database,id,inputDescProgram.text.toString())
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


    private fun showPopMenuSession(){
        buttonAddSessionProgram.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.menu_create_session -> {
                        val intent = Intent(this,SessionActivity::class.java)
                        finish()
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        true
                    }
                    R.id.menu_choose_session -> {
                        val intent = Intent(this,ListSessionActivity::class.java)
                        finish()
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu_session)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stu.manageActivityResult(requestCode, resultCode, data)
        imageUri = stu.imageUri
        imageViewCreateProg.setImageURI(imageUri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        stu.manageRequestPermissionResult(requestCode, grantResults)
    }

    private fun createSpinnerLevel(){
        val spinner: Spinner = findViewById(R.id.spinnerLevelProgram)

        ArrayAdapter.createFromResource(
                this,
                R.array.level_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {}

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
            }
        }
    }

}
