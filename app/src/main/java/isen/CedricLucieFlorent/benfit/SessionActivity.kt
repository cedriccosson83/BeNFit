package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_session.*
import kotlinx.android.synthetic.main.activity_splash.view.*


class SessionActivity : MenuActivity(){

    var array : ArrayList<String> ?= ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_session, frameLayout)
        Log.d("state", "oncreate")
        auth = FirebaseAuth.getInstance()
        val idUser = auth.currentUser?.uid
        if (idUser != null) {
            showExosSession(database, recyclerViewExoSession, idUser,this)
        }
        recyclerViewExoSession.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        createSpinnerLevel()
        showPopMenuExo()

        btnMinusRound.setOnClickListener {
            var round = editTextNumberSerie.text.toString().toInt()
            round -= 1
            editTextNumberSerie.setText(round.toString())
            Log.d("round", round.toString())
        }

        btnPlusRound.setOnClickListener {
            var round = editTextNumberSerie.text.toString().toInt()
            round += 1
            Log.d("round", round.toString())
            editTextNumberSerie.setText(round.toString())
        }

        btnSaveSession.setOnClickListener {
            if (idUser != null) {
                saveSession(database, idUser,inputNameSession.text.toString(),inputDescSession.text.toString(),spinnerLevelSession.selectedItem.toString(), editTextNumberSerie.text.toString().toInt() )
                Toast.makeText(this,"Séance sauvegardée!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this,SessionFeedActivity::class.java)
                
                finish()

                startActivity(intent)
                deleteExoSessionTemp(database, idUser)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("state", "onresume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("state", "onrestart")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "ondestroy")


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
            ) {
                Log.d("niveau", spinnerLevelSession.getItemAtPosition(position).toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
                //Log.d("niveau", spinnerLevelSession.getItemAtPosition(0).toString())
            }
        }
    }


}
